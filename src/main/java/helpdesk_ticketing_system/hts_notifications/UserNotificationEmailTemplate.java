package helpdesk_ticketing_system.hts_notifications;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;

class UserNotificationEmailTemplate
{
    private static volatile String emailBodyTemplate;
    public UserNotificationEmailTemplate() {
    }

    String getTemplateFor(Event notificationType) throws Exception {
        switch(notificationType){
            case RESOLVED:
                // read template from file if never read before
                if(emailBodyTemplate==null || emailBodyTemplate.isEmpty())
                {
                    ClassLoader classLoader = this.getClass().getClassLoader();
                    URL fileUrl = classLoader.getResource("issueResolvedEmailTemplate.html");
                    if(fileUrl==null)
                        throw new FileNotFoundException("issueResolvedEmailTemplate.html");
                    File templateFile = new File(fileUrl.getFile());
                    emailBodyTemplate = new String(Files.readAllBytes(templateFile.toPath()));
                }
                // send that template
                return emailBodyTemplate;
            default:
                return null;
        }
    }
}
