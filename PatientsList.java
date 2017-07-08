package drpatients;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PatientsList")
public class PatientsList {
 private List < Patient > doc;
 private AtomicInteger dlId;

 public PatientsList() {
  doc = new CopyOnWriteArrayList < Patient > ();
  dlId = new AtomicInteger();
 }

 @XmlElement
 @XmlElementWrapper(name = "patients")
 public List < Patient > getPatients() {
  return this.doc;
 }
 public void setPatients(List < Patient > doc) {
  this.doc = doc;
 }

 @Override
 public String toString() {
  String s = "";
  for (Patient d: doc) s += d.toString();
  return s;
 }

 public Patient find(int id) {
  Patient dl = null;
  // Search the list -- for now, the list is short enough that
  // a linear search is ok but binary search would be better if the
  // list got to be an order-of-magnitude larger in size.
  for (Patient d: doc) {
   if (d.getId() == id) {
    dl = d;
    break;
   }
  }
  return dl;
 }
 public int add(String doctor, String first, String second, String third) {
  int id = dlId.incrementAndGet();
  Patient d = new Patient();
  d.setDoctor(doctor);
  d.setFirst(first);
  d.setSecond(second);
  d.setThird(third);
  d.setId(id);
  doc.add(d);
  return id;
 }
}
