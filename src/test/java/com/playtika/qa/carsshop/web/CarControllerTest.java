package com.playtika.qa.carsshop.web;

import com.playtika.qa.carsshop.domain.Car;
import com.playtika.qa.carsshop.domain.CarInStore;
import com.playtika.qa.carsshop.service.CarService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class CarControllerTest {
    @Mock
    private CarService carService;

    private CarController controller;

    private MockMvc mockMvc;

    @Before
    public void init() {
        controller = new CarController(carService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void addCarReturnId() throws Exception {
        Car car = new Car(1, "", "", 1);
        CarInStore carInStore = new CarInStore(car, 10, "cont");

        when(carService.addCarToStore(carInStore)).thenReturn(carInStore);
        long id = controller.createCar(10, "cont", car);
        assertThat(id, is(1L));
    }

    @Test
    public void getCarReturnsCarInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("price", 1);
        response.put("contact", "cont");
        when(carService.getCar(1)).thenReturn(response);
        assertThat(controller.getCar(1).get("price"), is(response.get("price")));
        assertThat(controller.getCar(1).get("contact"), is(response.get("contact")));
        assertThat(controller.getCar(1).size(), is(2));
    }

    @Test
    public void getCarReturnsEmptyCarInfoIfCarIsAbsent() {
        Map<String, Object> response = new HashMap<>();
        when(carService.getCar(1)).thenReturn(response);
        assertEquals(controller.getCar(1), response);
    }

    @Test
    public void getAllCarsReturnsCars() throws Exception {
        Map<Long, CarInStore> storedCars = new ConcurrentHashMap<>();
        storedCars.put(1L, new CarInStore(new Car(10, "red", "opel", 1), 1, "con"));
        storedCars.put(2L, new CarInStore(new Car(20, "blue", "mazda", 2), 1, "con"));
        when(carService.getAllCars()).thenReturn(storedCars.values());
        assertEquals(controller.getAllCars(), storedCars.values());
    }

    @Test
    public void getAllCarsReturnsEmptyResponseThenCarsAreAbsent() throws Exception {
        Map<Long, CarInStore> storedCars = new ConcurrentHashMap<>();
        when(carService.getAllCars()).thenReturn(storedCars.values());
        assertEquals(controller.getAllCars(), storedCars.values());
    }

    @Test
    public void deleteCar() throws Exception {
        mockMvc.perform(delete("/cars/3").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(carService).deleteCar(3);
    }
}