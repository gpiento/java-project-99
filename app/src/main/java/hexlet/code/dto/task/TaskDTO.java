package hexlet.code.dto.task;

import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.users.UserDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {

    private String name;
    private String description;
    private TaskStatusDTO taskStatus;
    private UserDTO assignee;
}
