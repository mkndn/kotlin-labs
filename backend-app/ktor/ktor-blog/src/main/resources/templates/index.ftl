<#-- @ftlvariable name="articles" type="kotlin.collections.List<com.mkdn.models.Article>" -->
<#import "_layout.ftl" as layout>
<@layout.header>
    <#list articles?reverse as article>
        <div>
            <p><a href="/articles/${article.id}">${article.title}</a></p>
        </div>
    </#list>
    <hr>
    <section>
        <a href="/articles/new">Create new article</a>
    </section>
</@layout.header>