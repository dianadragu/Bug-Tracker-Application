package entities.users;

import fileio.DeveloperInput;
import fileio.ManagerInput;
import fileio.ReporterInput;

public class UserLoaderVisitor {
    public User createUser(ReporterInput input) {
        return Reporter.builder()
                .username(input.getUsername())
                .email(input.getEmail())
                .role(UserRole.valueOf(input.getRole()))
                .build();
    }

    public User createUser(DeveloperInput input) {
        return Developer.builder()
                .username(input.getUsername())
                .email(input.getEmail())
                .role(UserRole.valueOf(input.getRole()))
                .hireDate(input.getHireDate())
                .expertiseArea(ExpertiseArea.valueOf(input.getExpertiseArea()))
                .seniority(DevSeniority.valueOf(input.getSeniority()))
                .build();
    }

    public User createUser(ManagerInput input) {
        return Manager.builder()
                .username(input.getUsername())
                .email(input.getEmail())
                .role(UserRole.valueOf(input.getRole()))
                .hireDate(input.getHireDate())
                .subordinates(input.getSubordinates())
                .build();
    }

}
