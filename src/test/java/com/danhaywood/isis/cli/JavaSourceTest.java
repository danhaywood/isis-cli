package com.danhaywood.isis.cli;

import com.google.common.base.Predicates;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaSourceTest {

    final String source = "public class Customer { /** \n* test comment\n */ private String firstName; private String lastName; public void foo() {} }";
    final String toInsert = "public void bar(){int x=3;}";
    private JavaSource javaSource;

    @Before
    public void setUp() throws Exception {
        javaSource = new JavaSource(source);
    }

    @Test
    public void constructor_pretty_prints() throws Exception {

        final String source = javaSource.getSource();
        assertThat(source).isEqualTo(
                          "public class Customer {" + System.lineSeparator()
                        + "    /** " + System.lineSeparator()
                        + "     * test comment"+ System.lineSeparator()
                        + "     */"+ System.lineSeparator()
                        + "    private String firstName;"+ System.lineSeparator()
                        + "    private String lastName;"+ System.lineSeparator()
                        + ""+ System.lineSeparator()
                        + "    public void foo() {"+ System.lineSeparator()
                        + "    }"+ System.lineSeparator()
                        + "}");
    }

    @Test
    public void inserted() throws Exception {

        javaSource.insert(toInsert, null, null);

        final String source = javaSource.getSource();
        assertThat(source).isEqualTo(
                          "public class Customer {" + System.lineSeparator()
                        + "    /** " + System.lineSeparator()
                        + "     * test comment"+ System.lineSeparator()
                        + "     */"+ System.lineSeparator()
                        + "    private String firstName;"+ System.lineSeparator()
                        + "    private String lastName;"+ System.lineSeparator()
                        + ""+ System.lineSeparator()
                        + "    public void foo() {"+ System.lineSeparator()
                        + "    }"+ System.lineSeparator()
                        + ""+ System.lineSeparator()
                        + "    public void bar() {"+ System.lineSeparator()
                        + "        int x = 3;"+ System.lineSeparator()
                        + "    }"+ System.lineSeparator()
                        + "}");
    }

    @Test
    public void inserted_not_vetoed_on_field() throws Exception {

        javaSource.insert(toInsert, Predicates.<FieldDeclaration>alwaysFalse(), null);

        final String source = javaSource.getSource();
        assertThat(source).contains("public void bar()");
    }

    @Test
    public void inserted_not_vetoed_on_method() throws Exception {

        javaSource.insert(toInsert, null, Predicates.<MethodDeclaration>alwaysFalse());

        final String source = javaSource.getSource();
        assertThat(source).contains("public void bar()");
    }

    @Test
    public void inserted_vetoed_on_field() throws Exception {

        javaSource.insert(toInsert, Predicates.<FieldDeclaration>alwaysTrue(), null);

        assertThat(javaSource.getSource()).doesNotContain("bar");
    }
    @Test
    public void inserted_vetoed_on_method() throws Exception {

        javaSource.insert(toInsert, null, Predicates.<MethodDeclaration>alwaysTrue());
        assertThat(javaSource.getSource()).doesNotContain("bar");
    }

}