package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.Exception.ProductionError;
import com.driver.Exception.ProductionError;
import com.driver.Transformer.WebSeriesTransformer;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception, ProductionError{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        WebSeries webSeries = WebSeriesTransformer.convertDtoToEntity(webSeriesEntryDto);
        WebSeries checkSeries = webSeriesRepository.findBySeriesName(webSeries.getSeriesName());
        if(checkSeries != null){
            throw new Exception("Series is already present");
        }
        int id = webSeriesEntryDto.getProductionHouseId();
        Optional<ProductionHouse> productionHouseOpt = productionHouseRepository.findById(id);
        ProductionHouse productionHouse = productionHouseOpt.get();
        if(productionHouse == null){
            throw new ProductionError("Production house is not present");
        }

        webSeries.setProductionHouse(productionHouse);
        webSeries = webSeriesRepository.save(webSeries);

        List<WebSeries> seriesList = productionHouse.getWebSeriesList();
        seriesList.add(webSeries);

        double productionHouseRating = 0;
        for(WebSeries series : seriesList){
            productionHouseRating += series.getRating();
        }
        int seriesCount = seriesList.size();
        productionHouseRating = productionHouseRating / seriesCount;

        productionHouse.setRatings(productionHouseRating);

        productionHouseRepository.save(productionHouse);

        return webSeries.getId();
    }

}