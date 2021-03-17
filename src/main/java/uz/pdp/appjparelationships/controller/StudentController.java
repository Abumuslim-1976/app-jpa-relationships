package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    AddressRepository addressRepository;


    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }


    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentByFacultyId(@PathVariable Integer facultyId, @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPageByFacultyId = studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
        return studentPageByFacultyId;
    }

    //4. GROUP OWNER
    @GetMapping("/forGroup/{groupId}")
    public Page<Student> getStudentByGroupId(@PathVariable Integer groupId, @RequestParam Integer page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> allByGroupId = studentRepository.findAllByGroupId(groupId, pageable);
        return allByGroupId;
    }

    @PostMapping
    public String createStudent(@RequestBody StudentDto studentDto) {
        Optional<Group> groupById = groupRepository.findById(studentDto.getGroupId());
        if (!groupById.isPresent())
            return "Bunday ID lik group yo`q";

        List<Subject> subjectList = subjectRepository.findAllById(studentDto.getSubjectId());

        Student student = new Student();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        student.setGroup(groupById.get());
        student.setSubjects(subjectList);

        Address address = new Address();
        address.setCity(studentDto.getCity());
        address.setDistrict(studentDto.getDistrict());
        address.setStreet(studentDto.getStreet());
        Address saveAddress = addressRepository.save(address);

        student.setAddress(saveAddress);
        studentRepository.save(student);
        return "Student saqlandi";
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Integer id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (!optionalStudent.isPresent())
            return "bunday ID lik student yo`q";

        studentRepository.deleteById(id);
        return "Student o`chirildi";
    }

    @PutMapping("/{id}")
    public String editStudent(@PathVariable Integer id, @RequestBody StudentDto studentDto) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {

            Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
            if (!optionalGroup.isPresent())
                return "Bunday ID lik group yo`q";

            List<Subject> subjectList = subjectRepository.findAllById(studentDto.getSubjectId());

            Student student = optionalStudent.get();
            student.setFirstName(studentDto.getFirstName());
            student.setLastName(studentDto.getLastName());
            student.setGroup(optionalGroup.get());
            student.setSubjects(subjectList);

            Address address = student.getAddress();
            address.setCity(studentDto.getCity());
            address.setDistrict(studentDto.getDistrict());
            address.setStreet(studentDto.getStreet());
            Address saveAddress = addressRepository.save(address);

            student.setAddress(saveAddress);
            studentRepository.save(student);
            return "Student tahrirlandi";
        }
        return "Student topilmadi";
    }


}




