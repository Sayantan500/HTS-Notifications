package helpdesk_ticketing_system.hts_notifications;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class EventHandler implements RequestHandler<Map<String,Object>,Object> {
    @Override
    public Object handleRequest(Map<String, Object> inputEvent, Context context) {
        return null;
    }
}
