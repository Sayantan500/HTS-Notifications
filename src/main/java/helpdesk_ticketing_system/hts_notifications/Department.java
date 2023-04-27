package helpdesk_ticketing_system.hts_notifications;

public class Department {
    public enum DepartmentTypes{
        ACCOUNTS,
        EXAM_CELL
    }

    public static Channel getDepartmentChannel(DepartmentTypes d){
        switch(d){
            case ACCOUNTS:
                return Channel.ACCOUNTS;
            case EXAM_CELL:
                return Channel.EXAM_CELL;
        }
        return null;
    }
}
