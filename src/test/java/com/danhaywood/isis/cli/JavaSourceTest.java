package com.danhaywood.isis.cli;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaSourceTest {

    final String source = "public class Customer { private String firstName; private String lastName; public void foo() {} }";
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
                          "public class Customer {\n"
                        + "  private String firstName;\n"
                        + "  private String lastName;\n"
                        + "  public void foo(){\n"
                        + "  }\n"
                        + "}\n");
    }

    @Test
    public void inserted() throws Exception {

        javaSource.insert(toInsert, Predicates.<BodyDeclaration>alwaysFalse());

        final String source = javaSource.getSource();
        assertThat(source).isEqualTo(
                          "public class Customer {\n"
                        + "  private String firstName;\n"
                        + "  private String lastName;\n"
                        + "  public void foo(){\n"
                        + "  }\n"
                        + "  public void bar(){\n"
                        + "    int x=3;\n"
                        + "  }\n"
                        + "}\n");
    }

    @Test
    public void inserted_vetoed_on_field() throws Exception {

        javaSource.insert(toInsert, new Predicate<BodyDeclaration>() {
            public boolean apply(final BodyDeclaration bodyDeclaration) {
                return bodyDeclaration instanceof FieldDeclaration;
            }
        });

        assertThat(javaSource.getSource()).doesNotContain("bar");
    }
    @Test
    public void inserted_vetoed_on_method() throws Exception {

        javaSource.insert(toInsert, new Predicate<BodyDeclaration>() {
            public boolean apply(final BodyDeclaration bodyDeclaration) {
                return bodyDeclaration instanceof MethodDeclaration;
            }
        });

        assertThat(javaSource.getSource()).doesNotContain("bar");
    }
}