package org.nerd.kid.api;


import com.scienceminer.nerd.kb.Concept;
import com.scienceminer.nerd.kb.UpperKnowledgeBase;

public class LookupNerdTest {
    public static void main(String[] args) throws Exception{
        Concept concept = UpperKnowledgeBase.getInstance().getConcept("Q76");
        if (concept != null) {
            System.out.println(concept.getId());
            System.out.println("en pageId:" + concept.getPageIdByLang("en"));
        }
    }
}
