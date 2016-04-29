package com.avaje.ebeaninternal.server.cache;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.bean.EntityBean;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;
import com.avaje.ebeaninternal.server.transaction.DefaultPersistenceContext;
import com.avaje.tests.model.basic.Address;
import com.avaje.tests.model.basic.Car;
import com.avaje.tests.model.basic.Customer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CachedBeanDataFromBeanTest extends BaseTestCase {

  SpiEbeanServer server = spiEbeanServer();

  @Test
  public void extract() throws Exception {

    BeanDescriptor<Customer> desc = server.getBeanDescriptor(Customer.class);

    Customer customer = new Customer();
    customer.setId(42);
    customer.setName("Rob");

    Address billingAddress = new Address();
    billingAddress.setId(Short.valueOf("12"));
    billingAddress.setCity("SomePlace");

    customer.setBillingAddress(billingAddress);

    CachedBeanData cacheData = CachedBeanDataFromBean.extract(desc, (EntityBean) customer);

    assertEquals(cacheData.getData("id"), "42");
    assertEquals(cacheData.getData("name"), "Rob");
    assertEquals(cacheData.getData("billingAddress"), "12");
  }


  @Test
  public void inheritance() {

    Car car = new Car();
    car.setId(42);
    car.setDriver("Jimmy");
    car.setNotes("some notes");

    BeanDescriptor<Car> carDesc  = server.getBeanDescriptor(Car.class);
    CachedBeanData cacheData = CachedBeanDataFromBean.extract(carDesc, (EntityBean) car);

    Car newCar = new Car();
    EntityBean entityBean = (EntityBean)newCar;
    CachedBeanDataToBean.load(carDesc, entityBean, cacheData, new DefaultPersistenceContext());

    assertEquals(newCar.getId(), car.getId());
    assertEquals(newCar.getDriver(), car.getDriver());
    assertEquals(newCar.getNotes(), car.getNotes());
  }
}