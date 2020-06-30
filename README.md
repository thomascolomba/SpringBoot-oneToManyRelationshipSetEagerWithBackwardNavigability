CRUD operations on a OneToMany relationship with a java Set type with Eager fetch, backward navigability on that relationship (bonus : Lombok to eliminate boilerplate code).<br/>
An instance of A has a Set of B.<br/>
An instance of B has a reference to the A instance it is related to.<br/>
A <><--0..*-----1-- B<br/>
<br/>
compile & execute :<br/>
mvn spring-boot:run<br/>
compile into fat jar then execute :<br/>
mvn clean package<br/>
java -jar target/oneToManyRelationshipEagerWithBackwardNavigability-0.0.1-SNAPSHOT.jar<br/>
<br/>
To Compile from within Eclipse or any other IDE, you need to install Lombok : https://projectlombok.org/setup/overview<br/>
<br/>
<br/>
During the execution, the console shows : <br/>
===== <b>Persisting As and Bs</b><br/>
===== As<br/>
A{id=1, myString='myString1' Bs : 11 12 }<br/>
A{id=2, myString='myString2' Bs : 21 22 }<br/>
A{id=3, myString='myString3' Bs : }<br/>
===== Bs<br/>
B{id=1, myInt=11, a.myString=myString1}<br/>
B{id=2, myInt=12, a.myString=myString1}<br/>
B{id=3, myInt=21, a.myString=myString2}<br/>
B{id=4, myInt=22, a.myString=myString2}<br/>
===== <b>Modifying some As and Bs</b><br/>
===== As<br/>
A{id=1, myString='myModifiedString1' Bs : 12 }<br/>
A{id=2, myString='myModifiedString2' Bs : -11 21 22 }<br/>
A{id=3, myString='myString3' Bs : }<br/>
===== Bs<br/>
B{id=1, myInt=-11, a.myString=myModifiedString2}<br/>
B{id=2, myInt=12, a.myString=myModifiedString1}<br/>
B{id=3, myInt=21, a.myString=myModifiedString2}<br/>
B{id=4, myInt=22, a.myString=myModifiedString2}<br/>
===== <b>Deleting some As and Bs</b><br/>
===== As<br/>
A{id=2, myString='myModifiedString2' Bs : -11 21 }<br/>
A{id=3, myString='myString3' Bs : }<br/>
===== Bs<br/>
B{id=1, myInt=-11, a.myString=myModifiedString2}<br/>
B{id=3, myInt=21, a.myString=myModifiedString2}<br/>
<br/>

--A.java (entity that holds a collection of B entities)<br/>
//@ToString <b>We can't use the lombok generated toString method because executing it leads to infinite loop.</b><br/>
...<br/>
private String myString;<br/>
<b>@OneToMany(mappedBy = "a", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)<br/>
private Set&lt;B&gt; bSet;</b><br/>
we use <b>orphanRemoval = true</b> so a B will be removed from the database when it is removed from its corresponding A's collection.<br/>

--B.java (entity related to a A entity)<br/>
private int myInt;<br/>
<b>@ManyToOne<br/>
@JoinColumn(name = "a_id", nullable = false)<br/>
private A a;</b><br/>
a is a reference to the A instance that holds the B instance. It allows backward navigability (we can write b.getA() to retrieve the A instance holding this B instance).<br/>

--ARepository.java<br/>
public List&lt;A&gt; findByMyString(String myString);<br/>
--BRepository.java<br/>
List&lt;B&gt; findByMyInt(int myInt);<br/>

--AccessingDataJpaApplication.java (main class)<br/>
log.info("===== Persisting As and Bs");<br/>
<b>persistData</b>(aRepository, bRepository);<br/>
readData(aRepository, bRepository);<br/>
log.info("===== Modifying some As and Bs");<br/>
<b>modifyData</b>(aRepository, bRepository);<br/>
readData(aRepository, bRepository);<br/>
log.info("===== Deleting some As and Bs");<br/>
<b>deleteData</b>(aRepository, bRepository);<br/>
readData(aRepository, bRepository);<br/>
...<br/>
<b>persistData(){</b><br/>
&nbsp;&nbsp;//we build A without nested Bs, we set A to each B<br/>
&nbsp;&nbsp;A a1 = new A("myString1");<br/>
&nbsp;&nbsp;A a2 = new A("myString2");<br/>
&nbsp;&nbsp;aRepository.save(a1);<br/>
&nbsp;&nbsp;aRepository.save(a2);<br/>
&nbsp;&nbsp;bRepository.save(new B(11, a1));<br/>
&nbsp;&nbsp;bRepository.save(new B(12, a1));<br/>
&nbsp;&nbsp;bRepository.save(new B(21, a2));<br/>
&nbsp;&nbsp;bRepository.save(new B(22, a2));<br/>
&nbsp;&nbsp;//we can build an A without Bs<br/>
&nbsp;&nbsp;A a3 = new A("myString3");<br/>
&nbsp;&nbsp;aRepository.save(a3);<br/>
}<br/>
<b>modifyData(){</b><br/>
//we change a1.myString and a2.myString and we affect a B previously affect at a1 to a2<br/>
&nbsp;&nbsp;A a1 = aRepository.findByMyString("myString1").get(0);<br/>
&nbsp;&nbsp;A a2 = aRepository.findByMyString("myString2").get(0);<br/>
&nbsp;&nbsp;a1.setMyString("myModifiedString1");<br/>
&nbsp;&nbsp;a2.setMyString("myModifiedString2");<br/>
&nbsp;&nbsp;B b = bRepository.findByMyInt(11).get(0);<br/>
&nbsp;&nbsp;b.setMyInt(-11);<br/>
&nbsp;&nbsp;b.setA(a2);<br/>
&nbsp;&nbsp;aRepository.save(a1);<br/>
&nbsp;&nbsp;aRepository.save(a2);<br/>
&nbsp;&nbsp;bRepository.save(b);<br/>
}<br/>
<b>deleteData(){</b><br/>
//we delete 1 A and 1 B related to another A<br/>
A a1 = aRepository.findByMyString("myModifiedString1").get(0);<br/>
aRepository.delete(a1);<br/>
//we do not delete the B instance -> we want to remove that B instance from the A's list<br/>
A a2 = aRepository.findByMyString("myModifiedString2").get(0);<br/>
a2.getBSet().removeIf((B b) -> b.getMyInt() == 22);<br/>
aRepository.save(a2);<br/>
}<br/>