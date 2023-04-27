package helpdesk_ticketing_system.hts_notifications;

import com.amazonaws.services.lambda.runtime.Context;
import com.slack.api.Slack;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

public class SlackClient
{
    private final Slack slack;
    private final String BOT_OAUTH_TOKEN;

    public SlackClient() {
        slack = Slack.getInstance();
        BOT_OAUTH_TOKEN = System.getenv("bot_OAuth_token");
    }

    public boolean sendMessage(String message, String channelID, Context context)
    {
        try {
            ChatPostMessageResponse response = slack.methods(BOT_OAUTH_TOKEN).chatPostMessage(
                    ChatPostMessageRequest.builder()
                            .channel(channelID)
                            .text(message)
                            .build()
            );
            if(response.isOk())
            {
                context.getLogger().log(response.toString());
                return true;
            }
        } catch (Exception e) {
            context.getLogger().log("Exception thrown in : " + this.getClass().getName());
            context.getLogger().log("Exception class : " + e.getClass().getName());
            context.getLogger().log("Exception message : " + e.getMessage());
        }
        return false;
    }
}
