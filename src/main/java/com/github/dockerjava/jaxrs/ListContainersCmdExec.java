package com.github.dockerjava.jaxrs;

import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static com.google.common.net.UrlEscapers.urlPathSegmentEscaper;

public class ListContainersCmdExec extends AbstrSyncDockerCmdExec<ListContainersCmd, List<Container>> implements
        ListContainersCmd.Exec {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListContainersCmdExec.class);

    public ListContainersCmdExec(WebTarget baseResource, DockerClientConfig dockerClientConfig) {
        super(baseResource, dockerClientConfig);
    }

    @Override
    protected List<Container> execute(ListContainersCmd command) {
        WebTarget webTarget = getBaseResource().path("/containers/json").queryParam("since", command.getSinceId())
                .queryParam("before", command.getBeforeId());

        webTarget = booleanQueryParam(webTarget, "all", command.hasShowAllEnabled());
        webTarget = booleanQueryParam(webTarget, "size", command.hasShowSizeEnabled());

        if (command.getLimit() != null && command.getLimit() >= 0) {
            webTarget = webTarget.queryParam("limit", String.valueOf(command.getLimit()));
        }

        if (command.getFilters() != null) {
            webTarget = webTarget
                    .queryParam("filters", urlPathSegmentEscaper().escape(command.getFilters().toString()));
        }

        LOGGER.trace("GET: {}", webTarget);
        List<Container> containers = webTarget.request().accept(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Container>>() {
                });
        LOGGER.trace("Response: {}", containers);

        return containers;
    }

}
