//*************************************************//
//          INTHER LOGISTICS ENGINEERING           //
//*************************************************//
package alisgroup.titanicmanipulator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author agore
 */
public class Main {

  private static final String REGEX_TITLE = "\\b[A-Za-z]*\\.";
  private static final String REGEX_NAME = "^[\\p{L} '-]+";

  public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    CSV csv = new CSV();
    List<Person> persons = csv.readPersons("src\\main\\resources\\total.csv");

    Pattern paternTitle = Pattern.compile(REGEX_TITLE);
    Pattern paternName = Pattern.compile(REGEX_NAME);
    persons.stream().forEach(
            p -> {
              String name = p.getName();
              Matcher mTitle = paternTitle.matcher(name);
              if (mTitle.find()) {
                p.setTitle(mTitle.group());
              }
              Matcher mName = paternName.matcher(name);
              if (mName.find()) {
                p.setName(mName.group(0));
              }
              if (p.getFare() != 1000) {
                p.setFare(p.getFare() / (p.getSibSp() + p.getParCh() + 1));
              }
            });

    populateFare(persons, 0.015f, 2000);
    reduceTitles(persons);
    List<Person> personsMr = new ArrayList<>();
    List<Person> personsMrs = new ArrayList<>();
    List<Person> personsMiss = new ArrayList<>();
    List<Person> personsMaster = new ArrayList<>();
    persons.stream().forEach(p -> {
      switch (p.getTitle()) {
        case "Mr.":
          personsMr.add(p);
          break;
        case "Mrs.":
          personsMrs.add(p);
          break;
        case "Miss.":
          personsMiss.add(p);
          break;
        case "Master.":
          personsMaster.add(p);
          break;
      }
    });
//     To be more precise, these titles should be treated each.
    populateAge(personsMr, 0.0015f, 5000);
    populateAge(personsMrs, 0.001f, 10000);
    populateAge(personsMiss, 0.0004f, 10000);
    populateAge(personsMaster, 0.01f, 5000);

    populateSurvivance(personsMr, 0.0015f, 7000);
    populateSurvivance(personsMrs, 0.001f, 7000);
    populateSurvivance(personsMiss, 0.0004f, 500);
    populateSurvivance(personsMaster, 0.01f, 2000);

//    List<Person> personsWithSurvivance = new ArrayList<>();
//    List<Person> personsWithoutSurvivance = new ArrayList<>();
//    persons.stream().forEach(p -> {
//      if (Math.round(p.getSurvived()) == 2) {
//        personsWithoutSurvivance.add(p);
//      } else {
//        personsWithSurvivance.add(p);
//      }
//    });
    csv.exportCSV(persons, "src\\main\\resources\\results.csv");

//Just a test data
//    float x[][] = {{1, 3, 4}, {1, 5, 7}, {1, 7, 6}, {1, 4, 8}, {1, 5, 9}};
//    float y[] = {3, 4, 5, 3, 7};
//    Matrix m = new Matrix(x, y);
//    float theta[] = Gradient.calculateTheta(m, 0.01f, 50);
//    for(int i = 0; i < theta.length; i++){
//      System.out.println(theta[i]);
//    }
//Should obtain : 0.04705481; 0.3663616; 0.38562405
  }

  private static void populateFare(List<Person> persons, float alpha, int iterations) throws NoSuchMethodException {

    List<Person> personsWithFair = new ArrayList<>();
    List<Person> personsWithoutFair = new ArrayList<>();
    persons.stream().forEach(p -> {
      if (Math.round(p.getFare()) == 1000d) {
        personsWithoutFair.add(p);
      } else {
        personsWithFair.add(p);
      }
    });
    Matrix m = new Matrix();
    try {
      m = m.createMatrix(personsWithFair, Person.class.getMethod("getpClass"),
              Person.class.getMethod("getParChSibSp"), Person.class.getMethod("getEmbarked"),
              Person.class.getMethod("getFare"));
    } catch (InvocationTargetException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
    double[] theta = Gradient.calculateTheta(m, alpha, iterations);

    personsWithoutFair.stream().forEach(p -> {
      double fare = 0;
      try {
        fare = Gradient.calculateCost(theta, p, Person.class.getMethod("getpClass"),
                Person.class.getMethod("getParChSibSp"), Person.class.getMethod("getEmbarked"));
      } catch (NoSuchMethodException ex) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
      } catch (SecurityException ex) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
      }
      p.setFare(fare);

    });
  }

  private static void populateAge(List<Person> persons, float alpha, int iterations) throws NoSuchMethodException {
    List<Person> personsWithAge = new ArrayList<>();
    List<Person> personsWithoutAge = new ArrayList<>();
    persons.stream().forEach(p -> {
      if (Math.round(p.getAge()) == 1000f) {
        personsWithoutAge.add(p);
      } else {
        personsWithAge.add(p);
      }
    });
    Matrix m = new Matrix();
    try {
      m = m.createMatrix(personsWithAge, Person.class.getMethod("getpClass"), Person.class.getMethod("getFare"), Person.class.getMethod("getpClass"), Person.class.getMethod("getSibSp"),
              Person.class.getMethod("getParCh"), Person.class.getMethod("getSibSp"), Person.class.getMethod("getEmbarked"),
              Person.class.getMethod("getAge"));
    } catch (InvocationTargetException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
    double[] theta = Gradient.calculateTheta(m, alpha, iterations);

    personsWithoutAge.stream().forEach(p -> {
      double age = 0;
      try {
        age = Gradient.calculateCost(theta, p, Person.class.getMethod("getpClass"), Person.class.getMethod("getFare"), Person.class.getMethod("getpClass"), Person.class.getMethod("getSibSp"),
                Person.class.getMethod("getParCh"), Person.class.getMethod("getSibSp"), Person.class.getMethod("getEmbarked"));
      } catch (NoSuchMethodException ex) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
      } catch (SecurityException ex) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
      }
      p.setAge(age);

    });
  }

  private static void populateSurvivance(List<Person> persons, float f, int i) {
    List<Person> personsWithSurvivance = new ArrayList<>();
    List<Person> personsWithoutSurvivance = new ArrayList<>();
    persons.stream().forEach(p -> {
      if (Math.round(p.getSurvived()) == 2) {
        personsWithoutSurvivance.add(p);
      } else {
        personsWithSurvivance.add(p);
      }
    });

    Matrix m = new Matrix();
    try {
      m = m.createMatrix(personsWithSurvivance, Person.class.getMethod("getParChSibSpProduce"),
              Person.class.getMethod("getFairPClass"), Person.class.getMethod("getEmbarked"),
              Person.class.getMethod("getAge"), Person.class.getMethod("getSurvived"));
    } catch (InvocationTargetException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NoSuchMethodException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    } catch (SecurityException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
    double[] theta = Gradient.calculateLogisticTheta(m, f, i);

    personsWithoutSurvivance.stream().forEach(p -> {
      double survivance = 0;
      try {
        survivance = Math.round(Gradient.sigmoid(Gradient.calculateCost(theta, p, Person.class.getMethod("getParChSibSpProduce"),
              Person.class.getMethod("getFairPClass"), Person.class.getMethod("getEmbarked"),
              Person.class.getMethod("getAge"))));
      } catch (NoSuchMethodException ex) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
      } catch (SecurityException ex) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
      }
      p.setSurvived(survivance);
    });
  }

  private static void reduceTitles(List<Person> persons) {
    persons.stream().forEach(p -> {
      switch (p.getTitle()) {
        case "Capt.":
          p.setTitle("Mr.");
          break;
        case "Col.":
          p.setTitle("Mr.");
          break;
        case "Countess.":
          p.setTitle("Mrs.");
          break;
        case "Don.":
          p.setTitle("Mr.");
          break;
        case "Dona.":
          p.setTitle("Mrs.");
          break;
        case "Jonkheer.":
          p.setTitle("Mr.");
          break;
        case "Major.":
          p.setTitle("Mr.");
          break;
        case "Lady.":
          p.setTitle("Mrs.");
          break;
        case "Ms.":
          p.setTitle("Miss.");
          break;
        case "Mlle.":
          p.setTitle("Miss.");
          break;
        case "Mme.":
          p.setTitle("Mrs.");
          break;
        case "Rev.":
          p.setTitle("Mr.");
          break;
        case "Sir.":
          p.setTitle("Mr.");
          break;
        case "Dr.": {
          if (Math.round(p.getSex()) == 0f) {
            p.setTitle("Mr.");
            break;
          } else {
            p.setTitle("Mrs.");
          }
        }
        default:
          break;
      }
    });
//Capt.         1
//Col.          4
//Countess.	1
//Don.          1
//Dona.         1
//Dr.           8
//Jonkheer.	1
//Lady.         1
//Major.	2
//Master.	61
//Miss.         260
//Mlle.         2
//Mme.          1
//Mr.           757
//Mrs.          197
//Ms.           2
//Rev.          8
//Sir.          1
// We have to reduce all these classes just in some general that really can help us in determining the age and maybe in future for survivance
// Finally we should have: Mr. Mrs. Miss. Master.
  }
}
