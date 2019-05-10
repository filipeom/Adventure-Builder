package pt.ulisboa.tecnico.softeng.broker.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure;
import pt.ulisboa.tecnico.softeng.broker.services.local.BrokerInterface;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.BrokerData;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.BulkData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping(value = "/brokers/{brokerCode}/bulks/{bulkId}")
public class RoomController {
  private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

  @RequestMapping(value = "/roomInfo", method = RequestMethod.GET)
  public String showRoomInformation(Model model, @PathVariable String brokerCode,
                                                @PathVariable String bulkId) {
    logger.info("showRoomInformation brokerCode:{}, bulkId:{}", brokerCode, bulkId);

    BrokerData brokerData = BrokerInterface.getBrokerDataByCode(brokerCode, BrokerData.CopyDepth.BULKS);

    if (brokerData == null) {
      model.addAttribute("error", "Error: it does not exist a broker with the code " + brokerCode);
      model.addAttribute("broker", new BrokerData());
      model.addAttribute("brokers", BrokerInterface.getBrokers());
      return "brokers";
    }

    List<BulkData> bulks = brokerData.getBulks();

    if (bulks == null) {
      //declare error
      model.addAttribute("error", "Error: Bulk with id " + bulkId + " does not exist ");
      model.addAttribute("broker", new BrokerData());
      model.addAttribute("brokers", BrokerInterface.getBrokers());
      return "brokers";
    }

    List<String> references = null;

    for (BulkData bulk : bulks)
      if (bulk.getId().equals(bulkId)) {
        references = bulk.getReferences();
      }

    if (references == null) {
      //declare error
      model.addAttribute("error", "Error: it does not exists references for bulk  " + bulkId);
      model.addAttribute("broker", new BrokerData());
      model.addAttribute("brokers", BrokerInterface.getBrokers());
      return "brokers";
    }

    HotelInterface hi = new HotelInterface();

    List<RestRoomBookingData> rooms = new ArrayList<>();


    for (String ref : references) {
      RestRoomBookingData room = hi.getRoomBookingData(ref);
      room.setPrice(room.getPrice() / Adventure.SCALE);
      rooms.add(room);
    }

    model.addAttribute("brokerCode", brokerCode);
    model.addAttribute("bulkId", bulkId);
    model.addAttribute("rooms", rooms);
    return "rooms";
  }



  @RequestMapping(value = "/{reference}/cancel", method = RequestMethod.POST)
  public String cancelRoom(Model model, @PathVariable String brokerCode,
                                        @PathVariable String bulkId,
                                        @PathVariable String reference) {

    logger.info("cancelRoom brokerCode:{}, bulkId:{}, reference:{}", brokerCode, bulkId, reference);


    HotelInterface hi = new HotelInterface();

    try {
      hi.cancelBooking(reference);
    } catch (Exception e) {
      model.addAttribute("error", "Error: could not cancel hotel room");
      model.addAttribute("broker", new BrokerData());
      model.addAttribute("brokers", BrokerInterface.getBrokers());
      return "brokers";
    }

    return "redirect:/brokers/" + brokerCode + "/bulks/" + bulkId+ "/roomInfo";
  }
}
