package top.woodwhale.blog.services.impl;

import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.SimTocExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.woodwhale.blog.dao.CategoryDao;
import top.woodwhale.blog.pojo.Article;
import top.woodwhale.blog.pojo.PageList;
import top.woodwhale.blog.pojo.SearchResult;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.ISolrService;
import top.woodwhale.blog.utils.TextUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static top.woodwhale.blog.utils.Constants.Article.ARTICLE_MARKDOWN_TYPE;
import static top.woodwhale.blog.utils.Constants.Category.CATEGORY_REMOVE_STATE;
import static top.woodwhale.blog.utils.Constants.Page.DEFAULT_PAGE;
import static top.woodwhale.blog.utils.Constants.Page.MAX_SIZE;

/**
 * solr的搜索类
 */
@Slf4j
@Service
public class SolrServiceImpl implements ISolrService {

    /**
     * 自动注入的solrClient
     */
    @Autowired
    private HttpSolrClient solrClient;

    /**
     * 自动注入的categoryDao层
     */
    @Autowired
    private CategoryDao categoryDao;

    @Override
    public ResponseResult doSearch(String keyword, int page, int size, String categoryId, Integer sort) {
        // 设置页数和每页容量
        page = Math.max(page, DEFAULT_PAGE);
        size = Math.min(MAX_SIZE, Math.max(size, 1));
        // 设置每页的数量
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setRows(size);
        solrQuery.setStart((page - 1) * size);
        // 设置关键字
        solrQuery.set(CommonParams.DF, "search_item");
        if (TextUtils.isEmpty(keyword)) {
            solrQuery.set(CommonParams.Q, "*");
        } else {
            solrQuery.set(CommonParams.Q, keyword);
        }
        // 排序：时间升序（1），时间降序（2）。浏览量升序（3），浏览量降序（4）
        if (sort != null) {
            switch (sort) {
                case 1:
                    solrQuery.setSort("blog_create_time", SolrQuery.ORDER.asc);
                    break;
                case 2:
                    solrQuery.setSort("blog_create_time", SolrQuery.ORDER.desc);
                    break;
                case 3:
                    solrQuery.setSort("blog_view_count", SolrQuery.ORDER.asc);
                    break;
                case 4:
                    solrQuery.setSort("blog_view_count", SolrQuery.ORDER.desc);
                    break;
                default:
                    return ResponseResult.FAILED(TextUtils.notExist("排序方式"));
            }
        }
        // 添加分类
        if (!TextUtils.isEmpty(categoryId)) {
            if (categoryDao.findOneById(categoryId) == null ||
                    categoryDao.findOneById(categoryId).getState().equals(CATEGORY_REMOVE_STATE)) {
                return ResponseResult.FAILED(TextUtils.notExist("分类"));
            }
            solrQuery.setFilterQueries("blog_category_id" + categoryId);
        }
        // 关键字高亮
        solrQuery.setHighlight(true);
        solrQuery.addHighlightField("blog_title,blog_content");
        solrQuery.setHighlightSimplePre("<font color='red'>");
        solrQuery.setHighlightSimplePost("</font>");
        solrQuery.setHighlightFragsize(100);
        // 设置返回字段
        solrQuery.addFacetField("id", "blog_content", "blog_create_time", "blog_labels",
                "blog_url", "blog_title", "blog_view_count");
        // 搜索
        try {
            // 处理搜索结果
            QueryResponse result = solrClient.query(solrQuery);
            // 获取高亮内容
            Map<String, Map<String, List<String>>> highlighting = result.getHighlighting();
            // 把数据转为bean类
            List<SearchResult> resultList = result.getBeans(SearchResult.class);
            for (SearchResult searchResult : resultList) {
                Map<String, List<String>> stringListMap = highlighting.get(searchResult.getId());
                if (stringListMap != null) {
                    List<String> blogContent = stringListMap.get("blog_content");
                    if (blogContent != null) {
                        long len = blogContent.get(0).length();
                        if (len > 200) {
                            searchResult.setBlogContent(blogContent.get(0).substring(0, 500));
                        } else {
                            searchResult.setBlogContent(blogContent.get(0));
                        }
                    }
                    List<String> blogTitle = stringListMap.get("blog_title");
                    if (blogTitle != null) {
                        searchResult.setBlogTitle(blogTitle.get(0));
                    }
                }
            }
            // 返回搜索结果
            PageList<SearchResult> pageList = new PageList<>();
            pageList.setContents(resultList);
            long numFound = result.getResults().getNumFound();
            pageList.setTotalCount(numFound);
            pageList.setPageSize(size);
            pageList.setCurrentPage(page);
            long totalPage = numFound / size;
            pageList.setTotalPage(totalPage);
            boolean isFirst = page == 1;
            pageList.setFirst(isFirst);
            boolean isLast = page == totalPage;
            pageList.setLast(isLast);

            return ResponseResult.SUCCESS("solr搜索成功").setData(pageList);
        } catch (Exception e) {
            log.error("solr搜索错误", e);
            return ResponseResult.FAILED(TextUtils.smtError("solr搜索"));
        }
    }

    /**
     * 添加文章到solr
     *
     * @param article 文章bean类
     */
    @Override
    public void addArticle(Article article) {
        String type = article.getType();
        String html;
        if (type.equals(ARTICLE_MARKDOWN_TYPE)) {
            // markdown格式，先转为html
            MutableDataSet options = new MutableDataSet().set(Parser.EXTENSIONS, Arrays.asList(
                    TablesExtension.create(),
                    JekyllTagExtension.create(),
                    TocExtension.create(),
                    SimTocExtension.create()
            ));
            Parser parser = Parser.builder(options).build();
            HtmlRenderer renderer = HtmlRenderer.builder(options).build();
            Node document = parser.parse(article.getContent());
            html = renderer.render(document);
        } else {
            html = article.getContent();
        }
        // html转为text
        String content = Jsoup.parse(html).text();
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", article.getId());
        doc.addField("blog_title", article.getTitle());
        doc.addField("blog_view_count", article.getViewCount());
        doc.addField("blog_create_time", article.getCreateTime());
        doc.addField("blog_labels", article.getLabels());
        doc.addField("blog_url", "https://www.woodwhale.top");
        doc.addField("blog_category_id", article.getCategoryId());
        doc.addField("blog_content", content);
        try {
            solrClient.add(doc);
            solrClient.commit();
            log.info("文章id " + article.getId() + " 的solr添加/更新成功!");
        } catch (Exception e) {
            log.error("solr添加失败", e);
        }
    }

    /**
     * 通过id删除solr
     *
     * @param id 文章id
     */
    @Override
    public void deleteArticle(String id) {
        try {
            solrClient.deleteById(id);
            solrClient.commit();
            log.info("文章id " + id + " 的solr删除成功!");
        } catch (Exception e) {
            log.error("solr删除失败", e);
        }
    }

    /**
     * solr中更新article
     *
     * @param article Article
     */
    @Override
    public void updateArticle(String articleId, Article article) {
        article.setId(articleId);
        this.addArticle(article);
    }
}
