API Template "Framework"
========================
A very very simple framework (can we really call it that?) for REST API using 
JAX-RS, with modules for storage (using JPA) that follows the Template 
Pattern.  
  
The Idea
--------
The idea was to use the DRY principle for my project, as I was rewriting the 
same thing over and over again. So I used a lot of generic and implement the 
classes that I usually have in a way that they can be reused. When I saw, there 
was this kind of framework that I have been using since.  
  
The purpose
-----------
So, now I'm doing microservices, or trying to. And the main problem that I see  
is that I was shifting the complexity of my solutions to my infrastructure, 
that is not "that great". Then, I kind of wanted to have this unicorn framework 
in which I could easily change from monolithic to microservice by only changing 
my DAO layer.  
I realized that I already had a framework, and with a bit more of generics, I 
came with this solution, where you have the Resource plugged to a Service, that 
does access data using a DAO instance. That instance can be a Storage or a 
Client implementation.  
Supposing that our Domain classes are not anemic, we can build our API in a way 
that we can easily switch from monolithic to microservices.  
  
The Dependencies
----------------
I tried to make this the smallest and more customizable possible.  
You should add your own dependencies because there is none to most of the 
libraries in this project. That because I used APIs for specifications mostly.  
That said, you need to include the implementation yourself.