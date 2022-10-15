package tutorschedule.mapper;

import tutor_schedule.api.model.student.StudentDto;
import tutor_schedule.api.model.teacher.TeacherDto;
import tutorschedule.entity.Student;
import tutorschedule.entity.Teacher;

public class UserConverter {
    private UserConverter() {
    }

    public static StudentDto toStudentDto(Student student) {
        return StudentDto.builder()
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .password(student.getPassword())
                .dateOfBirth(student.getDateOfBirth())
                .build();
    }

    public static TeacherDto toTeacherDto(Teacher teacher) {
        return TeacherDto.builder()
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .email(teacher.getEmail())
                .password(teacher.getPassword())
                .dateOfBirth(teacher.getDateOfBirth())
                .subjects(teacher.getSubjects())
                .build();
    }

    public static Student studentDtoToStudent(StudentDto studentDto) {
        return Student.builder()
                .firstName(studentDto.getFirstName())
                .lastName(studentDto.getLastName())
                .email(studentDto.getEmail())
                .password(studentDto.getPassword())
                .dateOfBirth(studentDto.getDateOfBirth())
                .build();
    }

    public static Teacher teacherDtoToTeacher(TeacherDto teacherDto) {
        return Teacher.builder()
                .firstName(teacherDto.getFirstName())
                .lastName(teacherDto.getLastName())
                .email(teacherDto.getEmail())
                .password(teacherDto.getPassword())
                .dateOfBirth(teacherDto.getDateOfBirth())
                .subjects(teacherDto.getSubjects())
                .build();
    }
}
