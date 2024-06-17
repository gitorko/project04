package com.demo.project04.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataPageEvictionMode;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IgniteConfig {

    /**
     * Override the node name for each instance at start using properties
     */
    @Value("${ignite.nodeName:node0}")
    private String nodeName;

    @Value("${ignite.kubernetes.enabled:false}")
    private Boolean k8sEnabled;

    @Bean(name = "igniteInstance")
    public Ignite igniteInstance() {
        Ignite ignite = Ignition.start(igniteConfiguration());
        return ignite;
    }

    @Bean(name = "igniteConfiguration")
    public IgniteConfiguration igniteConfiguration() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        /**
         * Uniquely identify node in a cluster use consistent Id.
         */
        cfg.setConsistentId(nodeName);

        cfg.setIgniteInstanceName("my-ignite-instance");
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setLocalHost("127.0.0.1");
        cfg.setMetricsLogFrequency(0);

        cfg.setCommunicationSpi(tcpCommunicationSpi());
        if (k8sEnabled) {
            cfg.setDiscoverySpi(tcpDiscoverySpiKubernetes());
        } else {
            cfg.setDiscoverySpi(tcpDiscovery());
        }
        cfg.setDataStorageConfiguration(dataStorageConfiguration());
        cfg.setCacheConfiguration(cacheConfiguration());
        return cfg;
    }

    @Bean(name = "cacheConfiguration")
    public CacheConfiguration[] cacheConfiguration() {
        List<CacheConfiguration> cacheConfigurations = new ArrayList<>();
        cacheConfigurations.add(getLockCacheConfig());
        return cacheConfigurations.toArray(new CacheConfiguration[cacheConfigurations.size()]);
    }

    private CacheConfiguration getLockCacheConfig() {
        /**
         * Country cache to store key value pair
         */
        CacheConfiguration cacheConfig = new CacheConfiguration("lock-cache");
        /**
         * This cache will be stored in non-persistent data region
         */
        cacheConfig.setDataRegionName("my-data-region");
        /**
         * Needs to be transactional for getting distributed lock
         */
        cacheConfig.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        return cacheConfig;
    }

    /**
     * Nodes discover each other over this port
     */
    private TcpDiscoverySpi tcpDiscovery() {
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        tcpDiscoverySpi.setIpFinder(ipFinder);
        tcpDiscoverySpi.setLocalPort(47500);
        // Changing local port range. This is an optional action.
        tcpDiscoverySpi.setLocalPortRange(9);
        //tcpDiscoverySpi.setLocalAddress("localhost");
        return tcpDiscoverySpi;
    }

    private TcpDiscoverySpi tcpDiscoverySpiKubernetes() {
        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        TcpDiscoveryKubernetesIpFinder ipFinder = new TcpDiscoveryKubernetesIpFinder();
        spi.setIpFinder(ipFinder);
        return spi;
    }

    /**
     * Nodes communicate with each other over this port
     */
    private TcpCommunicationSpi tcpCommunicationSpi() {
        TcpCommunicationSpi communicationSpi = new TcpCommunicationSpi();
        communicationSpi.setMessageQueueLimit(1024);
        communicationSpi.setLocalAddress("localhost");
        communicationSpi.setLocalPort(48100);
        communicationSpi.setSlowClientQueueLimit(1000);
        return communicationSpi;
    }

    private DataStorageConfiguration dataStorageConfiguration() {
        DataStorageConfiguration dsc = new DataStorageConfiguration();
        DataRegionConfiguration defaultRegionCfg = new DataRegionConfiguration();
        DataRegionConfiguration regionCfg = new DataRegionConfiguration();

        defaultRegionCfg.setName("default-data-region");
        defaultRegionCfg.setInitialSize(10 * 1024 * 1024); //10MB
        defaultRegionCfg.setMaxSize(50 * 1024 * 1024); //50MB
        defaultRegionCfg.setPersistenceEnabled(false);
        defaultRegionCfg.setPageEvictionMode(DataPageEvictionMode.RANDOM_LRU);

        regionCfg.setName("my-data-region");
        regionCfg.setInitialSize(10 * 1024 * 1024); //10MB
        regionCfg.setMaxSize(50 * 1024 * 1024); //50MB
        regionCfg.setPersistenceEnabled(false);

        dsc.setDefaultDataRegionConfiguration(defaultRegionCfg);
        dsc.setDataRegionConfigurations(regionCfg);

        return dsc;
    }

}
