package com.netflix.discovery.shared.transport.jersey;

import com.sun.jersey.client.apache4.ApacheHttpClient4;

/**
 * @author David Liu
 */
public interface EurekaJerseyClient { /* 专门配合JerseyApplicationClient使用的*/

    ApacheHttpClient4 getClient();

    /**
     * Clean up resources.
     */
    void destroyResources();
}
