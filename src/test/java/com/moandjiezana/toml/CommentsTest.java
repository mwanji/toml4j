package com.moandjiezana.toml;

import org.junit.Test;

public class CommentsTest
{

    @Test
    public void should_handle_table_array_in_comment_after_table_array_definition()
    {
        new Toml().read("[[example]] # [[]]\n a=1");
        new Toml().read("[[example]] # [[abc]]\n a=1");
        new Toml().read("[[example]] # [[abc]]");
    }

    @Test
    public void should_handle_table_array_in_comment_after_table_definition()
    {
        new Toml().read("[example] # [[]]");
        new Toml().read("[example] # [[abc]]");
        new Toml().read("[example] # [[]]\n a=1");
    }

}
