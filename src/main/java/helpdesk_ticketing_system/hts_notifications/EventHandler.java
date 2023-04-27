package helpdesk_ticketing_system.hts_notifications;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.net.HttpURLConnection;
import java.util.Map;

public class EventHandler implements RequestHandler<Map<String,Object>,Object> {
    private final SlackClient slackClient;

    public EventHandler() {
        slackClient = new SlackClient();
    }

    @Override
    public Object handleRequest(Map<String, Object> inputEvent, Context context) {
        if(inputEvent==null || inputEvent.isEmpty())
            return HttpURLConnection.HTTP_BAD_REQUEST;
        try
        {
            Event event = Event.valueOf(String.valueOf(inputEvent.get("event-type")));
            String message=null, channelID=null;
            switch(event)
            {
                case NEW_ISSUE:
                    message = String.format("New Issue Raised with ID : %s",inputEvent.get("issue_id"));
                    channelID = Channels.getChannelID(Channel.HELPDESK);
                    break;
            }// end switch block

            if(message!=null && channelID!=null && slackClient.sendMessage(message,channelID,context))
                return HttpURLConnection.HTTP_OK;

            return HttpURLConnection.HTTP_BAD_REQUEST;
        }
        catch (Exception e)
        {
            context.getLogger().log("Exception thrown in : " + this.getClass().getName());
            context.getLogger().log("Exception class : " + e.getClass().getName());
            context.getLogger().log("Exception message : " + e.getMessage());
        }
        return HttpURLConnection.HTTP_CLIENT_TIMEOUT;
    }
}
