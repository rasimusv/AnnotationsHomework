import com.google.auto.service.AutoService;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@AutoService(Processor.class)
@SupportedAnnotationTypes(value = {"HtmlForm", "HtmlInput"})
public class HtmlProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(HtmlForm.class);
        for (Element element : annotatedElements) {
            // получаем полный путь для генерации html и чтения ftlh
            String templateName = element.getSimpleName().toString() + ".ftlh";
            String path = "target/classes/html/" + element.getSimpleName().toString() + ".html";
            Path out = Paths.get(path);
            try {
                //Freemarker init
                Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
                configuration.setDefaultEncoding("UTF-8");
                configuration.setTemplateLoader(new FileTemplateLoader(new File("src/main/resources/ftl")));
                Template template = configuration.getTemplate(templateName);

                //Getting Annotations
                //--For HtmlForm
                HtmlForm formAnnotation = element.getAnnotation(HtmlForm.class);
                Map<String, String> formAnnotationAttr = new HashMap<>();
                formAnnotationAttr.put("action", formAnnotation.action());
                formAnnotationAttr.put("method", formAnnotation.method());
                log.error("Form head отработал!");

                //--For HtmlInput
                List <Map<String, String>> formInnerLines = new ArrayList<>();
                element.getEnclosedElements()
                        .stream()
                        .map(x -> x.getAnnotation(HtmlInput.class))
                        .filter(Objects::nonNull)
                        .forEach(x -> {
                            Map<String, String> lineAttrs = new HashMap<>();
                            lineAttrs.put("type", x.type());
                            lineAttrs.put("name", x.name());
                            lineAttrs.put("placeholder", x.placeholder());
                            formInnerLines.add(lineAttrs);
                            log.error("Form inner отработал!!");
                        });

                //Attributes for template
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("formAnnotationAttr", formAnnotationAttr);
                attributes.put("formInnerLines", formInnerLines);


                //Writing
                BufferedWriter writer = new BufferedWriter(new FileWriter(out.toFile()));
                try {
                    template.process(attributes, writer);
                } catch (TemplateException e) {
                    throw new IllegalStateException(e);
                }
                writer.close();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return true;
    }
}
