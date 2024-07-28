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
import com.intellij.psi.xml.XmlAttributeValue;
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
    if (!(element instanceof XmlAttributeValue)) {
      return;
    }

    XmlAttributeValue attributeValue = (XmlAttributeValue) element;
    XmlTag xmlTag = (XmlTag) attributeValue.getParent().getParent();

    System.out.println(xmlTag.getName());

    if (isMyBatisTag(xmlTag.getName())) {
      String interfaceName = xmlTag.getAttributeValue(
          "interface"); // 인터페이스 이름이 "interface" 속성에 있다고 가정합니다.
      if (interfaceName != null) {
        PsiClass[] classes = PsiShortNamesCache.getInstance(project)
            .getClassesByName(interfaceName, GlobalSearchScope.allScope(project));
        if (classes.length > 0) {
          PsiClass psiClass = classes[0];
          showMethodParameters(psiClass);
        }
      }
    }

  }

  private boolean isMyBatisTag(String tagName) {
    return "insert".equals(tagName) || "update".equals(tagName) ||
        "delete".equals(tagName) || "select".equals(tagName) || "sql".equals(tagName);
  }

  private void showMethodParameters(PsiClass psiClass) {
    StringBuilder parametersInfo = new StringBuilder("Methods and their parameters:\n");
    for (PsiMethod method : psiClass.getMethods()) {
      parametersInfo.append(method.getName()).append("(");
      for (PsiParameter parameter : method.getParameterList().getParameters()) {
        parametersInfo.append(parameter.getType().getPresentableText()).append(" ")
            .append(parameter.getName()).append(", ");
      }
      if (method.getParameterList().getParametersCount() > 0) {
        parametersInfo.setLength(parametersInfo.length() - 2); // 마지막 쉼표와 공백 제거
      }
      parametersInfo.append(")\n");
    }
    Messages.showMessageDialog(parametersInfo.toString(), "Method Parameters",
        Messages.getInformationIcon());
  }
}
