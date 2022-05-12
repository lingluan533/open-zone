package com.zms.openzone.search.controller;



import com.zms.openzone.search.service.SearchService;
import com.zms.openzone.search.utils.PageUtils;
import com.zms.openzone.search.vo.DiscussEsModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

/**
 * @author: zms
 * @create: 2022/2/12 13:14
 */
@Controller
@RequestMapping("search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @RequestMapping("/search")
    public String searchController(String keyWord, PageUtils page, Model model) throws IOException {
        List<DiscussEsModel> discussEsModels = searchService.searchPage(keyWord, page.getCurrent() - 1, page.getLimit());

        model.addAttribute("searchres", discussEsModels);
        model.addAttribute("keyword", keyWord);
        //分页信息
        page.setPath("http://search.lingluan.vip/search/search?keyword=" + keyWord);
        page.setRows(discussEsModels == null ? 0 : discussEsModels.size());
        model.addAttribute("page", page);
        return "site/search";
    }


}
