package helpdesk_ticketing_system.hts_notifications;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import jakarta.mail.MessagingException;

import java.net.HttpURLConnection;
import java.util.Map;

public class EventHandler implements RequestHandler<Map<String,Object>,Object> {
    private final SlackClient slackClient;
    private final EmailSender emailSender;
    private final UserNotificationEmailTemplate userNotificationEmailTemplate;

    public EventHandler() {
        slackClient = new SlackClient();
        emailSender = new EmailSender();
        userNotificationEmailTemplate = new UserNotificationEmailTemplate();
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
                case TICKET_RAISED:
                    context.getLogger().log("In TICKET_RAISED event case");
                    // getting the department enum for dept name that was sent in event
                    // this is the dept for whom the ticket was raised
                    Department.DepartmentTypes departmentType = Department.DepartmentTypes.valueOf(
                            String.valueOf(inputEvent.get("assigned_to")).toUpperCase()
                    );

                    // getting the channel name for that department
                    Channel channel = Department.getDepartmentChannel(departmentType);

                    // getting the channel ID
                    if(channel!=null)
                        channelID = Channels.getChannelID(channel);
                    else
                    {
                        context.getLogger().log("Channel not found for " + departmentType + "\n");
                        break;
                    }
                    message = String.format("New Ticket Raised with ID : %s",inputEvent.get("ticket_id"));
                    break;
                case RESOLVED:
                    context.getLogger().log("In RESOLVED event case");
                    String notificationRecipient = String.valueOf(inputEvent.get("submitted_by"));
                    String issueId = String.valueOf(inputEvent.get("issue_id"));
                    String subject = "ISSUE RESOLVED";
                    String bodyTemplate = userNotificationEmailTemplate.getTemplateFor(Event.RESOLVED);
                    String messageBody = String.format(
                            bodyTemplate,
                            notificationRecipient,
                            issueId,
                            System.getenv("app_name")
                    );
                    try{
                        emailSender.sendEmail(notificationRecipient,subject, messageBody);
                        return HttpURLConnection.HTTP_OK;
                    }catch (MessagingException e){
                        context.getLogger().log("Exception thrown in : " + this.getClass().getName() + "\n");
                        context.getLogger().log("Exception class : " + e.getClass().getName() + "\n");
                        context.getLogger().log("Exception message : " + e.getMessage() + "\n");
                    }
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
