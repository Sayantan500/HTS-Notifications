package helpdesk_ticketing_system.hts_notifications;

public class Channels {
    public static String getChannelID(Channel channel)
    {
        switch(channel){
            case HELPDESK:
                return System.getenv("channel_id_new_issues");
            case ACCOUNTS:
                return System.getenv("channel_id_accounts");
            case EXAM_CELL:
                return System.getenv("channel_id_exam_cell");
        }
        return null;
    }
}
