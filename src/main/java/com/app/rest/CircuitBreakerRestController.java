package com.app.rest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CircuitBreakerRestController {

    // URL for the API to fetch users
    String url = "https://dummyjson.com/users";

    @Autowired
    private RestTemplate restTemplate;


    @GetMapping("/name-list")
    @CircuitBreaker(name = "randamName", fallbackMethod = "sampleFallbackFun")
    public List<String> getUserNames() {
        // Make the request to the URL and get the response as a Users object
        ResponseEntity<UsersResponse> responseEntity = restTemplate.getForEntity(url, UsersResponse.class);

        // Get the body of the response
        UsersResponse usersResponse = responseEntity.getBody();

        // Check if the response is valid
        if (usersResponse != null && usersResponse.getUsers() != null) {
            // Extract first names from the list of users and return them
            return usersResponse.getUsers().stream()
                    .map(user -> "First Name :: " + "[" +user.getFirstName() + "]" +
                                      " Department :: " + "[" +user.getCompany().getDepartment()  + "]" +
                                      " Role :: " + "[" + user.getRole()  + "]" +
                                      " Address :: " + "[" + user.getAddress().getAddress()  + "]" +
                                      " State :: "+ "[" + user.getAddress().getState()  + "]" +
                                      " State Code :: "+ "[" + user.getAddress().getStateCode() + "]")
                    .collect(Collectors.toList());

        }

        return List.of(); // Return an empty list if no users were found
    }

    // Correct fallback method signature
    public List<String> sampleFallbackFun(Throwable throwable) {
        // Return an empty list or a default value when an error occurs
        System.out.println("Fallback triggered due to: " + throwable.getMessage());
        return List.of("Fallback: Service is down. Please try again later.");
    }



    // You may need to define the UsersResponse and User classes based on the API response
    public static class UsersResponse {
        private List<User> users; // List of users from the API response

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }
    }

    public static class User {
        private String firstName; // First name of the user
        private String role;
        private Company company;
        private Address address;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Company getCompany() {
            return company;
        }

        public void setCompany(Company company) {
            this.company = company;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public static class Company {
            private String department;

            public String getDepartment() {
                return department;
            }

            public void setDepartment(String department) {
                this.department = department;
            }
        }

        public static class Address {
            private String address;
            private String state;
            private String stateCode;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getStateCode() {
                return stateCode;
            }

            public void setStateCode(String stateCode) {
                this.stateCode = stateCode;
            }
        }
    }
}
