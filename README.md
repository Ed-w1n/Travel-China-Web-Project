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
![image](https://user-images.githubusercontent.com/81521033/180133106-83ab77ba-c729-4e87-b12f-974259afa32a.png)

Notice that native HTML is used throughout the project, so we need to use AJAX for posting data. If we use form to post data, the page must be refreshed every time it posts. 

First, regular expressions are used to restrict the input in JS. If the input does not meet the requirements, the input box will show red color. When cursor leaves focus of the input box, the content in the input box is also checked.

Notice that if the type of form is set as "submit", it will submit data as a form rather than using AJAX. So we need to set the type as "button" and set a click event. In this event, we write the code for checking input. Also, use $("#registerForm").serialize() to change to JSON format.

### Mail Activation
![image](https://user-images.githubusercontent.com/81521033/179387326-4638f520-dad7-403d-aa84-e6715891c343.png)
Generate a unique activation code using UUidUtils and set the status as "N" in registerUser() in UserServiceImpl. 

After clicking the link in Email, code will be given to activeUserServlet. In this servlet, whether the code is valid is checked. If code is not null, service will try to find the user object. If a user object is found, the status will be set as "Y" and the program will redirect to the login page.

Notice that if you want to use SMTP to send mail, some Email providers like Tencent require you to apply for an authorization code so that you use this code as password.

### Login
login.html
![image](https://user-images.githubusercontent.com/81521033/180133263-fb591ef9-d9ec-437b-aa08-20bbf52c992e.png)

![image](https://user-images.githubusercontent.com/81521033/180133525-009c9797-43ff-4e5a-87c9-c9660f265803.png)
After login, user's name will show on the top of the page as the red box shows. Firstly, user object will be sent to session and the page get this user object asynchronously when the main page loads. 

![image](https://user-images.githubusercontent.com/81521033/179449138-e89e23cf-ebc4-47ab-a5b4-982885c121ec.png)
The data for the title bar are from database. Actually, we can either store this data in MySQL database or store in Redis. Since we do not modify this data frequently, 
we could cache data in redis to improve efficiency.

    public List<Category> findTitle() {
        // Use Redis to store and get the category data
        List<Category> categories = new ArrayList<>();
        Set<Tuple> cateGory = jedis.zrangeWithScores("cateGory", 0, -1);
        if (cateGory == null || cateGory.size() == 0) {
            categories = dao.findTitle();
            for (int i = 0; i < categories.size(); i++) {
                jedis.zadd("cateGory", categories.get(i).getCid(), categories.get(i).getCname());
            }

        } else {
            for (Tuple tuple : cateGory) {
                Category category = new Category();
                category.setCname(tuple.getElement());
                category.setCid((int)tuple.getScore());
                categories.add(category);
            }
        }
        return categories;
    }
This is the code for redis in service layer. It can be seen that whether redis cache has the data is checked. If not, data will be stored in redis zset. Data in zset will be sorted automatically. We store the cid of categories so that when we get the data from redis the data is sorted. Use `jedis.zrangeWithScores()` to get tuple. Use `getElement()` to get real data. Use `getScore()` to get scores.

### Optimize Servlet
It is neither efficient nor neat to write servlet for every function. After analysis, we get the following diagram.
![image](https://user-images.githubusercontent.com/81521033/179454541-37a94a76-c126-4d23-86a2-582f688a04f0.png)
service() in HttpServlet can not be modified, so we need to create a BaseServlet to extend HttpServlet and change its service() so that distribution of methods are
realized. Then, we only need one servlet to extend HttpServlet for one module.

    public class BaseServlet extends HttpServlet {
        protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            String requestURI = request.getRequestURI();
            String methodName=requestURI.substring(requestURI.lastIndexOf("/")+1); // substring from last "/"
            
            try {

                Method method= this.getClass().getMethod(methodName,HttpServletRequest.class,HttpServletResponse.class);//获取所需方法名对应的方法
                method.invoke(this,request,response);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
           
        }
        public void writeValues( Object o,HttpServletResponse response){
            ObjectMapper mapper=new ObjectMapper();
            String json = null;
            try {
                json = mapper.writeValueAsString(o);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            try {
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

Override service() in BaseServlet. First get the name of method in uri, then use reflection to get the method and execute it. That is to say, we transform the original servlet into a method in the servlet of each independent module. The servlet of such a module originally corresponds to the creation of 10 servlets for different functions. Now, we only need to create 10 methods in the servlet of this module. Some code commonly used in servlets can also be extracted into baseservlet as public methods such as the writevalues method.

### Display in pages
![image](https://user-images.githubusercontent.com/81521033/180134329-a4725d6e-94da-4d8e-9530-eed4e250d764.png)

After clicking the bottom navigation page bar, it will jump to the corresponding page to display tourism route. The main code implementation is divided into two parts: the code function implementation of the bottom index part and the function implementation of the data display part.

We need to pass to the servlet the current page number, the tourism route index cid, and the number of routes displayed on each page. After processing in the servlet, only a pagebean object containing the total number of records, the total number of pages, the current page number, and the collection containing real data need to send back. The reason for sending cid is that cid is the foreign key of tab_route:
![image](https://user-images.githubusercontent.com/81521033/179460804-42f15870-3800-4855-bfe3-b2b264c2562a.png)

Dao Layer

    public class RouteDaoImpl implements RouteDao {
        private JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());

        @Override
        public int findTotalCount(int cid) {
            String sql = "select count(*) from tab_route where cid = ?";
            return template.queryForObject(sql,Integer.class,cid);
        }

        @Override
        public List<Route> findByPage(int cid, int start, int pageSize) {
            String sql = "select * from tab_route where cid = ? limit ? , ?";

            return template.query(sql,new BeanPropertyRowMapper<Route>(Route.class),cid,start,pageSize);
        }
    }

Data search is carried out using cid and start and end page numbers. Here, two functions are implemented to find the total number of data and the specific data of each page.

Service Layer

    public class RouteServiceImpl implements RouteService {
        private RouteDao routeDao = new RouteDaoImpl();
        @Override
        public PageBean<Route> pageQuery(int cid, int currentPage, int pageSize) {
            // Encapsulate PageBean
            PageBean<Route> pb = new PageBean<Route>();
            // Set current page number 
            pb.setCurrentPage(currentPage);
            // Set how many number to display on every page
            pb.setPageSize(pageSize);

            // Set total count
            int totalCount = routeDao.findTotalCount(cid);
            pb.setTotalCount(totalCount);
            // Set the list of the route displayed on current page
            int start = (currentPage - 1) * pageSize;//开始的记录数
            List<Route> list = routeDao.findByPage(cid,start,pageSize);
            pb.setList(list);

            // Total number of pages = total routes count / number of routes displayed in one page
            int totalPage = totalCount % pageSize == 0 ? totalCount / pageSize :(totalCount / pageSize) + 1 ;
            pb.setTotalPage(totalPage);


            return pb;
        }
    }

After obtaining the total number of data from Dao layer, it calculates the total number of pages according to the number of date displayed on each page. If the division is complete, there are exactly this many pages. If the division is not complete, there should be one more page to display the additional data. The start attribute is obtained by multiplying the current page number by the number of data displayed on each page.

Servlet Layer

    @WebServlet("/route/*")
    public class RouteServlet extends BaseServlet {

        private RouteService routeService = new RouteServiceImpl();

        /**
         * Page Query
         * @param request
         * @param response
         * @throws ServletException
         * @throws IOException
         */
        public void pageQuery(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            //1.Receive parameter
            String currentPageStr = request.getParameter("currentPage");
            String pageSizeStr = request.getParameter("pageSize");
            String cidStr = request.getParameter("cid");

            int cid = 0;
            //2.Process parameter
            if(cidStr != null && cidStr.length() > 0){
                cid = Integer.parseInt(cidStr);
            }
            int currentPage = 0;// default as first page
            if(currentPageStr != null && currentPageStr.length() > 0){
                currentPage = Integer.parseInt(currentPageStr);
            }else{
                currentPage = 1;
            }

            int pageSize = 0;// default number of routes: 5 
            if(pageSizeStr != null && pageSizeStr.length() > 0){
                pageSize = Integer.parseInt(pageSizeStr);
            }else{
                pageSize = 5;
            }

            //3. call service to query PageBean
            PageBean<Route> pb = routeService.pageQuery(cid, currentPage, pageSize);

            //4. pageBean to JSON
            writeValue(pb,response);

        }

    }
This servlet has one goal: to get the pagebean containing the required data. Here, set a default value for each page of display data and the current page number to make the first access successfully.

### Search
![image](https://user-images.githubusercontent.com/81521033/180133695-1c311b7d-2c34-4236-9843-9b6e059b6720.png)
![image](https://user-images.githubusercontent.com/81521033/180133821-e2bbc5f8-e007-49c8-9c51-ac66184935d4.png)


Dao Layer

    public List<Route> getRouteList(int cid, int start, int pageSize, String rname) {
        StringBuilder sql=new StringBuilder("select * from tab_route where 1=1") ;
        List p=new ArrayList<>();
        if(cid!=0){
            sql.append(" and cid = ? ");
            p.add(cid);
        }
        if(rname!=null&& rname.length()>0 && !"null".equals(rname)){
            sql.append(" and rname like ?");
            p.add("%"+rname+"%");
        }
        sql.append(" limit ? , ?");
        p.add(start);
        p.add(pageSize);
        List<Route> routes=jdbcTemplate.query(sql.toString(),new            BeanPropertyRowMapper<Route>(Route.class),p.toArray());
        return routes;
    }

    public int getTotalCount(int cid, String rname) {
       StringBuilder sql=new StringBuilder("select count(*) from tab_route where 1 = 1 ");
        List p=new ArrayList<>();
        if(cid!=0){
            sql.append(" and cid = ? ");
            p.add(cid);
        }
        if(rname!=null && rname.length()>0 &&!"null".equals(rname)){
            sql.append(" and rname like ?");
            p.add("%"+rname+"%");
        }
        int count = jdbcTemplate.queryForObject(sql.toString(), Integer.class,p.toArray());
        return count;
    }

Here we modify the previous method. It can be seen that we add an rname to the parameter and use CID and rname to conduct joint query. Here we use two skills.

1. In this fuzzy query, we can write select count (*) from tab first_ Route where 1 = 1 to ensure that the entire SQL statement is correct. If cid is not equal to 0, add an `and cid=?`. It's the same for rname. The reason for this is that both cid and rname can be 0.

2. We use a list to store conditions, and then in jdbctemplate Queryforobject() converts it into an array, which brings great flexibility to our program

Service Layer
There is less change in service layer. We want to have PageBean, so some parameters are changed.

Servlet

One thing need to be noted is when we get the rname value, the actual uploaded code can be jumbled because Tomcat does not have the ability to automatically deal with garbled code until tomcat8, so we have to deal with garbled code manually.

rname = new String(rname.getBytes(“iso-8859-1”), “utf-8”);

### Route Detail
![image](https://user-images.githubusercontent.com/81521033/180134190-a855cadb-d2e3-4638-97e1-a887b1878375.png)

![image](https://user-images.githubusercontent.com/81521033/180128230-8423dffc-d6e0-4a23-9bb5-15e918992ba1.png)

![image](https://user-images.githubusercontent.com/81521033/180130134-ef4aec19-8343-443f-8b03-673d71982d4d.png)

### Collection
check whether user added current route to collecion
![image](https://user-images.githubusercontent.com/81521033/180131145-0cbe6137-5a22-48f1-a6aa-378049a9f2a3.png)

hit the add to collections button
![image](https://user-images.githubusercontent.com/81521033/180131648-737e6965-1057-43c0-9649-833221463834.png)





