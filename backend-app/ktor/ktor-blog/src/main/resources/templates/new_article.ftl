<#import "_layout.ftl" as layout>
<@layout.header>
    <section>
        <h3>Create article</h3>
        <form method="post" action="/articles">
            <p>
                <label>
                    <input type="text" name="title">
                </label>
            </p>
            <p>
                <label>
                    <textarea name="body"></textarea>
                </label>
            </p>
            <p>
                <button type="submit">Create</button>
            </p>
        </form>
    </section>
</@layout.header>