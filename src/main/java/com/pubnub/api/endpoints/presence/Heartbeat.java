package com.pubnub.api.endpoints.presence;

import com.pubnub.api.core.PnResponse;
import com.pubnub.api.core.Pubnub;
import com.pubnub.api.core.PubnubException;
import com.pubnub.api.core.PubnubUtil;
import com.pubnub.api.core.models.Envelope;
import com.pubnub.api.endpoints.Endpoint;
import lombok.Builder;
import retrofit2.Call;
import retrofit2.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public class Heartbeat extends Endpoint<Envelope, Boolean> {

    private Pubnub pubnub;
    private Object state;
    private List<String> channels;
    private List<String> channelGroups;

    @Override
    protected boolean validateParams() {
        return true;
    }

    @Override
    protected Call<Envelope> doWork() {
        Map<String, Object> params = new HashMap<>();

        params.put("uuid", pubnub.getConfiguration().getUuid());
        params.put("heartbeat", pubnub.getConfiguration().getPresenceTimeout());

        if (channelGroups != null && channelGroups.size() > 0){
            params.put("channel-group", PubnubUtil.joinString(channelGroups, ","));
        }

        String channelsCSV;

        if (channels != null && channels.size() > 0) {
            channelsCSV = PubnubUtil.joinString(channels, ",");
        } else {
            channelsCSV = ",";
        }

        if (state != null) {
            params.put("state", state);
        }

        PresenceService service = this.createRetrofit(pubnub).create(PresenceService.class);
        return service.heartbeat(pubnub.getConfiguration().getSubscribeKey(), channelsCSV, params);
    }

    @Override
    protected PnResponse<Boolean> createResponse(Response<Envelope> input) throws PubnubException {
        PnResponse<Boolean> pnResponse = new PnResponse<Boolean>();
        pnResponse.fillFromRetrofit(input);
        pnResponse.setPayload(true);

        return pnResponse;
    }

    protected int getConnectTimeout() {
        return pubnub.getConfiguration().getConnectTimeout();
    }

    protected int getRequestTimeout() {
        return pubnub.getConfiguration().getNonSubscribeRequestTimeout();
    }

}