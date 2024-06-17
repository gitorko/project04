package com.demo.project04.controller;

import com.demo.project04.service.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    final Ignite ignite;

    final LockService lockService;

    @GetMapping("/info")
    public String getInfo() {
        DataRegionConfiguration drc = ignite.configuration().getDataStorageConfiguration().getDataRegionConfigurations()[0];
        StringBuilder sb = new StringBuilder();
        sb.append("IgniteConsistentId: " + ignite.configuration().getConsistentId());
        sb.append("\n");
        sb.append("IgniteInstanceName: " + ignite.configuration().getIgniteInstanceName());
        sb.append("\n");
        sb.append("CommunicationSpi.localPort: " + ((TcpCommunicationSpi) ignite.configuration().getCommunicationSpi()).getLocalPort());
        sb.append("\n");
        sb.append("DefaultDataRegion initial size: " + ignite.configuration().getDataStorageConfiguration().getDefaultDataRegionConfiguration().getInitialSize());
        sb.append("\n");
        sb.append("Size: " + drc.getName() + " initial size: " + drc.getInitialSize());
        sb.append("\n");
        for (String cacheName : ignite.cacheNames()) {
            sb.append("Cache in cluster: " + cacheName);
            sb.append("\n");
        }
        return sb.toString();
    }

    @GetMapping("/run-job/{seconds}")
    public String runJob(@PathVariable Integer seconds) {
        return lockService.runJob(seconds);
    }

}
