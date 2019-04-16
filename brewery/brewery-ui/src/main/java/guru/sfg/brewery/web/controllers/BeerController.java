package guru.sfg.brewery.web.controllers;


import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerInventory;
import guru.sfg.brewery.repositories.BeerInventoryRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@RequestMapping("/beers")
    @Controller

    public class BeerController {

       //ToDO: Add service
        private BeerRepository beerRepository;
        private BeerInventoryRepository beerInventoryRepository;

        public BeerController(BeerRepository beerRepository, BeerInventoryRepository beerInventoryRepository) {
            this.beerRepository = beerRepository;
            this.beerInventoryRepository=beerInventoryRepository;
        }

        @RequestMapping("/find")
        public String findBeers(Model model){
            model.addAttribute("beer", Beer.builder().build());
            return "beers/findBeers";
        }

    @GetMapping
    public String processFindFormReturnMany(Beer beer, BindingResult result, Model model){
       // find beers by name
        //ToDO: Add Service
        //ToDO: Get paging data from view
        Page<Beer> pagedResult = beerRepository.findAllByBeerName(beer.getBeerName(), createPageRequest(0,10,Sort.Direction.DESC,"beerName"));
        List<Beer> beerList = pagedResult.getContent();
        if (beerList.isEmpty()) {
            // no beers found
            result.rejectValue("beerName", "notFound", "not found");
            return "beers/findBeers";
        } else if (beerList.size() == 1) {
            // 1 beer found
            beer = beerList.get(0);
            return "redirect:/beers/" + beer.getId();
        } else {
            // multiple beers found
            model.addAttribute("selections", beerList);
            return "beers/beerList";
        }
    }


    @GetMapping("/{beerId}")
    public ModelAndView showBeer(@PathVariable UUID beerId) {
        ModelAndView mav = new ModelAndView("beers/beerDetails");
        //ToDO: Add Service
        mav.addObject(beerRepository.findById(beerId).get());
        return mav;
    }

    @GetMapping("/new")
    public String initCreationForm(Model model) {
        model.addAttribute("beer", Beer.builder().build());
        return "beers/createBeer";
    }

    @PostMapping("/new")
    public String processCreationForm(Beer beer) {
        //ToDO: Add Service
        Beer newBeer = Beer.builder()
                .beerName(beer.getBeerName())
                .beerStyle(beer.getBeerStyle())
                .minOnHand(beer.getMinOnHand())
                .price(beer.getPrice())
                .quantityToBrew(beer.getQuantityToBrew())
                .upc(beer.getUpc())
                .build();

       Beer savedBeer= beerRepository.save(newBeer);
       return "redirect:/beers/" + savedBeer.getId();
        }

    @GetMapping("/{beerId}/edit")
    public String initUpdateBeerForm(@PathVariable UUID beerId, Model model) {
        if(beerRepository.findById(beerId).isPresent())
              model.addAttribute("beer", beerRepository.findById(beerId).get());
        return "beers/createOrUpdateBeer";
    }

    @PostMapping("/{beerId}/edit")
    public String processUpdationForm(@Valid Beer beer, BindingResult result) {
        if (result.hasErrors()) {
            return "beers/createOrUpdateBeer";
        } else {
            //ToDO: Add Service
            Beer savedBeer =  beerRepository.save(beer);
            return "redirect:/beers/" + savedBeer.getId();
        }
    }

    private PageRequest createPageRequest(int page, int size, Sort.Direction sortDirection, String propertyName) {
        return PageRequest.of(page,
                size,
                new Sort(sortDirection, propertyName));
    }
    }

