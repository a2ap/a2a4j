package io.github.a2ap.core.server;

import io.github.a2ap.core.jsonrpc.JSONRPCRequest;
import io.github.a2ap.core.jsonrpc.JSONRPCResponse;
import reactor.core.publisher.Flux;

public interface Dispatcher {

    JSONRPCResponse dispatch(JSONRPCRequest request);

    Flux<JSONRPCResponse> dispatchStream(JSONRPCRequest request);
} 
