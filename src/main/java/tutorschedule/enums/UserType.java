package tutorschedule.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tutorschedule.entity.Student;
import tutorschedule.entity.Teacher;
import tutorschedule.entity.User;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum UserType {
    STUDENT(Student.class),
    TEACHER(Teacher.class);

    private final Class<? extends User> entityType;
    private static final Map<Class<? extends User>, UserType> entityTypesMap = new HashMap<>();

    static {
        for (UserType userType : values()) {
            entityTypesMap.put(userType.entityType, userType);
        }
    }

    public static UserType from(Class<? extends User> type) {
        return entityTypesMap.get(type);
    }
}
