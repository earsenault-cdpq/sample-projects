package ai.grakn.example;

import ai.grakn.Grakn;
import ai.grakn.GraknGraph;
import ai.grakn.concept.Entity;
import ai.grakn.concept.EntityType;
import ai.grakn.concept.Instance;
import ai.grakn.concept.RelationType;
import ai.grakn.concept.Resource;
import ai.grakn.concept.ResourceType;
import ai.grakn.concept.RoleType;
import ai.grakn.exception.GraknValidationException;

/**
 * The purpose of this class is to show you how to build the genealogy graph outlined in:
 * https://grakn.ai/pages/documentation/developing-with-java/graph-api.html
 */
public class Main {
    private static final String keyspace = "genealogy";

    //Roles
    private static RoleType spouse;
    private static RoleType spouse1;
    private static RoleType spouse2;
    private static RoleType wife;
    private static RoleType husband;
    private static RoleType parent;
    private static RoleType mother;
    private static RoleType father;
    private static RoleType child;
    private static RoleType son;
    private static RoleType daughter;

    //Resource Types
    private static ResourceType<String> gender;
    private static ResourceType<String> date;
    private static ResourceType<String> birthDate;
    private static ResourceType<String> deathDate;
    private static ResourceType<String> name;
    private static ResourceType<String> firstname;
    private static ResourceType<String> middlename;
    private static ResourceType<String> surname;
    private static ResourceType<String> identifier;
    private static ResourceType<String> notes;
    private static ResourceType<Long> degree;
    private static ResourceType<Long> confidence;

    //Relation Types
    private static RelationType relatives;
    private static RelationType marriage;
    private static RelationType parentship;

    //Entity Types
    private static EntityType person;
    private static EntityType event;
    private static EntityType wedding;
    private static EntityType funeral;
    private static EntityType christening;
    private static EntityType birth;
    private static EntityType death;

    public static void main(String [] args){
        System.out.println("=================================================================================================");
        System.out.println("|||||||||||||||||||||||||||||||||   Grakn Graph API  Example   ||||||||||||||||||||||||||||||||||");
        System.out.println("=================================================================================================");

        System.out.println("Creating graph . . .");
        try(GraknGraph graph = Grakn.factory(Grakn.IN_MEMORY, keyspace).getGraph()){
            System.out.println("Writing ontology . . .");
            writeOntology(graph);
            System.out.println("Writing sample marriage . . .");
            writeSampleMarriage();
            System.out.println("Writing sample parentship . . .");
            writeSamplePartnership();

            //TODO: Your turn use the ontology to write more data.

            graph.commit();
        } catch (GraknValidationException e) {
            e.printStackTrace();
        }
        System.out.println("Done");
    }

    /**
     * Writes the genealogy ontology
     */
    private static void writeOntology(GraknGraph graph){
        //Roles
        spouse = graph.putRoleType("spouse").setAbstract(true);
        spouse1 = graph.putRoleType("spouse1").superType(spouse);
        spouse2 = graph.putRoleType("spouse2").superType(spouse);
        husband = graph.putRoleType("husband").superType(spouse);
        wife = graph.putRoleType("wife").superType(spouse);

        parent = graph.putRoleType("parent");
        mother = graph.putRoleType("mother").superType(parent);
        father = graph.putRoleType("father").superType(parent);

        child = graph.putRoleType("child");
        son = graph.putRoleType("son").superType(child);
        daughter = graph.putRoleType("daughter").superType(child);

        //Resource Types
        gender = graph.putResourceType("gender", ResourceType.DataType.STRING);

        date = graph.putResourceType("date", ResourceType.DataType.STRING);
        birthDate = graph.putResourceType("birth-date", ResourceType.DataType.STRING).superType(date);
        deathDate = graph.putResourceType("death-date", ResourceType.DataType.STRING).superType(date);

        name = graph.putResourceType("name", ResourceType.DataType.STRING);
        firstname = graph.putResourceType("firstname", ResourceType.DataType.STRING).superType(name);
        middlename = graph.putResourceType("middlename", ResourceType.DataType.STRING).superType(name);
        surname = graph.putResourceType("surname", ResourceType.DataType.STRING).superType(name);

        identifier = graph.putResourceType("identifier", ResourceType.DataType.STRING);
        notes = graph.putResourceType("notes", ResourceType.DataType.STRING);
        degree = graph.putResourceType("degree", ResourceType.DataType.LONG);
        confidence = graph.putResourceType("confidence", ResourceType.DataType.LONG);

        //Relation Types
        relatives = graph.putRelationType("relatives").setAbstract(true);

        marriage = graph.putRelationType("marriage").superType(relatives);
        marriage.hasRole(spouse1).hasRole(spouse2).hasRole(husband).hasRole(wife);
        marriage.hasResource(date);

        parentship = graph.putRelationType("parentship").superType(relatives);
        parentship.hasRole(parent).hasRole(mother).hasRole(father).hasRole(child).hasRole(son).hasRole(daughter);

        //Entity Types
        person = graph.putEntityType("person");
        person.playsRole(spouse).playsRole(parent).
                playsRole(child).playsRole(mother).
                playsRole(father).playsRole(son).
                playsRole(husband).playsRole(wife);
        person.hasResource(gender);
        person.hasResource(birthDate);
        person.hasResource(deathDate);
        person.hasResource(identifier);
        person.hasResource(firstname);
        person.hasResource(middlename);
        person.hasResource(surname);

        event = graph.putEntityType("event");
        event.hasResource(degree);
        event.hasResource(confidence);
        event.hasResource(notes);
        event.hasResource(date);
        event.hasResource(identifier);

        wedding = graph.putEntityType("wedding").superType(event);

        funeral = graph.putEntityType("funeral").superType(event);
        funeral.hasResource(deathDate);

        christening = graph.putEntityType("christening").superType(event);
        christening.hasResource(deathDate);

        birth = graph.putEntityType("birth").superType(event);
        birth.hasResource(birthDate);
        birth.hasResource(firstname);
        birth.hasResource(middlename);
        birth.hasResource(surname);
        birth.hasResource(gender);

        death = graph.putEntityType("death").superType(event);
        death.hasResource(deathDate);
    }

    /**
     * Writes an example of a marriage relationship including all the entities and resources needed
     */
    private static void writeSampleMarriage(){
        //Adding a sample marriage
        //Lets create a husband
        //But first we need to define some resource which describe the husband
        Resource<String> homerFirstName = firstname.putResource("Homer");
        Resource<String> jMiddleName = middlename.putResource("J");
        Resource<String> simpsonSurname = surname.putResource("Simpson");
        Resource<String> male = gender.putResource("Male");

        //Now we can create the actual husband entity
        Entity homer = person.addEntity();
        homer.hasResource(homerFirstName);
        homer.hasResource(jMiddleName);
        homer.hasResource(simpsonSurname);
        homer.hasResource(male);

        //Now lets create the wife.
        //Again we need to define a few extra resources exclusive to the wife
        Resource<String> margeFirstName = firstname.putResource("Marge");
        Resource<String> female = gender.putResource("Female");

        //Turns out we can use some of our existing resources to create the wife
        Entity marge = person.addEntity();
        marge.hasResource(margeFirstName);
        marge.hasResource(jMiddleName);
        marge.hasResource(simpsonSurname);
        marge.hasResource(female);

        //I now pronounce you husband and wife:
        marriage.addRelation().putRolePlayer(husband, homer).putRolePlayer(wife, marge);
    }

    /**
     * Writes an example of a parentship relationship including all the entities and resources needed
     */
    private static void writeSamplePartnership(){
        //Now lets say our couple had a child.
        //Lets first create that child
        Resource<String> bartFirstName = firstname.putResource("Bart");
        Resource<String> jMiddleName = middlename.putResource("J"); //This resource already exists so we just getting it back
        Resource<String> simpsonSurname = surname.putResource("Simpson"); //Same for this one
        Resource<String> male = gender.putResource("Male"); //and this one

        //Let's create the child:
        Entity bart = person.addEntity();
        bart.hasResource(bartFirstName);
        bart.hasResource(jMiddleName);
        bart.hasResource(simpsonSurname);
        bart.hasResource(male);

        //Lets get the parents back
        //We know they have unique first name so we will be lazy and use those to get back the husband and wife
        Instance homer = firstname.putResource("Homer").owner();
        Instance marge = firstname.putResource("Marge").owner();

        //Congrats you guys have a son
        parentship.addRelation().putRolePlayer(mother, marge).putRolePlayer(father, homer).putRolePlayer(son, bart);
    }
}