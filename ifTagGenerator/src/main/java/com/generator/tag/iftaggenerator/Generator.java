package com.generator.tag.iftaggenerator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

public class Generator extends AnAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {

    Project project = e.getProject();
    if (project == null) {
      return;
    }

    PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
    if (file == null) {
      return;
    }

    PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);
    if (!(element instanceof XmlTag)) {
      return;
    }

    XmlTag xmlTag = (XmlTag) element;

    System.out.println("XML TAG >>>>> " + xmlTag.getName());

    String namespace = xmlTag.getParentTag().getAttributeValue("namespace");
    String methodId = xmlTag.getAttributeValue("id");

    if (namespace != null && methodId != null) {
      PsiClass[] classes = PsiShortNamesCache.getInstance(project)
          .getClassesByName(namespace, GlobalSearchScope.allScope(project));
      if (classes.length > 0) {
        PsiClass psiClass = classes[0];
        showMethodParameters(psiClass, methodId);
      }
    }


  }

  private boolean isMyBatisTag(String tagName) {
    return "insert".equals(tagName) || "update".equals(tagName) ||
        "delete".equals(tagName) || "select".equals(tagName) || "sql".equals(tagName);
  }

  private void showMethodParameters(PsiClass psiClass, String methodId) {
    for (PsiMethod method : psiClass.getMethods()) {
      if (method.getName().equals(methodId)) {
        StringBuilder parametersInfo = new StringBuilder(
            "Parameters for method " + methodId + ":\n");
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
          parametersInfo.append(parameter.getType().getPresentableText()).append(" ")
              .append(parameter.getName()).append("\n");
        }
        Messages.showMessageDialog(parametersInfo.toString(), "Method Parameters",
            Messages.getInformationIcon());
        return;
      }
    }
    Messages.showMessageDialog("No method found with id " + methodId, "Error",
        Messages.getErrorIcon());
  }
}
