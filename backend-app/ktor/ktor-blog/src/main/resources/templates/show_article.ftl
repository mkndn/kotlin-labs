<#-- @ftlvariable name="article" type="com.mkdn.models.Article" -->
<#import "_layout.ftl" as layout>
<@layout.header>
    <section>
        <h3>${article.title}</h3>
        <p>
            ${article.body}
        </p>
        <hr>
        <p>
            <a href="/articles/${article.id}/edit">Edit article</a>
        </p>
    </section>
</@layout.header>