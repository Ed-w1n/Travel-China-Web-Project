# Travel China Web Project
## Background
This is a website project for GNG 5124 course of University of Ottawa. At the beginning of the course, we had a crazy idea: we always develop website using popular framworks such as Spring, SpringMVC, React, etc. what if we develop a web project **without** using any of those frameworks? Then this project exists.

## Tools
Java, HTML, JQuery, CSS, AJAX, Bootstarp, MySql, Maven

For front-end, native HTML and JQuery are used. For back-end, there are three layers: web layer, service layer and dao layer.

### Web Layer

Servlet：used as controller

HTML：used for view

Filter：web filter

BeanUtils：used for data encapsulation

Jackson：used for JSON serialization

### Service Layer

Javamail：send mail using Java

### Dao Layer

Mysql：database

Druid：database connection pool

JdbcTemplate：tool for jdbc

### Table Structure
![image](https://user-images.githubusercontent.com/81521033/179344168-cb800c93-1aa9-4d7b-a331-9ebf372afea5.png)

## Main Features
### Register
![image](https://user-images.githubusercontent.com/81521033/179386143-1e44d086-a518-45dc-94a1-5b6df81292ff.png)

register.html
![image](https://user-images.githubusercontent.com/81521033/179386199-3f321260-554a-48c5-b995-33dce17de427.png)

Notice that native HTML is used throughout the project, so we need to use AJAX for posting data. If we use form to post data, the page must be refreshed every time it posts. 

First, regular expressions are used to restrict the input in JS. If the input does not meet the requirements, the input box will show red color. When cursor leaves focus of the input box, the content in the input box is also checked.

Notice that if the type of form is set as "submit", it will submit data as a form rather than using AJAX. So we need to set the type as "button" and set a click event. In this event, we write the code for checking input. Also, use $("#registerForm").serialize() to change to JSON format.

### Mail Activation
![image](https://user-images.githubusercontent.com/81521033/179387326-4638f520-dad7-403d-aa84-e6715891c343.png)
Generate a unique activation code using UUidUtils and set the status as "N" in registerUser() in UserServiceImpl. 

After clicking the link in Email, code will be given to activeUserServlet. In this servlet, whether the code is valid is checked. If code is not null, service will try to find the user object. If a user object is found, the status will be set as "Y" and the program will redirect to the login page.

Notice that if you want to use SMTP to send mail, some Email providers like Tencent require you to apply for an authorization code so that you use this code as password.

### Login

### Collection

### Search



