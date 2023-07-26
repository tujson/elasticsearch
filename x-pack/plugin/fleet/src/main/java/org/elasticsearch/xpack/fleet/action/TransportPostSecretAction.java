/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.fleet.action;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.HandledTransportAction;
import org.elasticsearch.client.internal.Client;
import org.elasticsearch.client.internal.OriginSettingClient;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.transport.TransportService;

import static org.elasticsearch.xpack.core.ClientHelper.FLEET_ORIGIN;

public class TransportPostSecretAction extends HandledTransportAction<PostSecretRequest, PostSecretResponse> {

    private final Client client;

    @Inject
    public TransportPostSecretAction(TransportService transportService, ActionFilters actionFilters, Client client) {
        super(PostSecretAction.NAME, transportService, actionFilters, PostSecretRequest::new);
        this.client = new OriginSettingClient(client, FLEET_ORIGIN);
    }

    protected void doExecute(Task task, PostSecretRequest request, ActionListener<PostSecretResponse> listener) {
        client.prepareIndex(".fleet-secrets")
            .setSource(request.source(), request.xContentType())
            .execute(
                ActionListener.wrap(
                    indexResponse -> listener.onResponse(new PostSecretResponse(indexResponse.status(), indexResponse.getId())),
                    listener::onFailure
                )
            ); // TODO: check impl and failure handling
    }
}
