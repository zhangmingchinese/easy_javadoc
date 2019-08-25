package com.star.easydoc.service;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.util.ThrowableRunnable;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class DocWriterService {
    private static final Logger LOGGER = Logger.getInstance(DocWriterService.class);

    public static void write(Project project, PsiElement psiElement, PsiElement target) {
        try {
            WriteCommandAction.writeCommandAction(project, psiElement.getContainingFile()).run(
                (ThrowableRunnable<Throwable>)() -> {
                    Editor editor = PsiUtilBase.findEditor(psiElement.getContainingFile());
                    if (editor != null) {
                        PsiDocumentManager.getInstance(psiElement.getProject())
                            .doPostponedOperationsAndUnblockDocument(editor.getDocument());
                    }

                    psiElement.getNode().addChild(target.getNode(), psiElement.getFirstChild().getNode());

                    CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(psiElement.getProject());
                    PsiElement javadocElement = psiElement.getFirstChild();
                    int startOffset = javadocElement.getTextOffset();
                    int endOffset = javadocElement.getTextOffset() + javadocElement.getText().length();
                    codeStyleManager.reformatText(psiElement.getContainingFile(), startOffset, endOffset + 1);
                });
        } catch (Throwable throwable) {
            LOGGER.error("写入错误", throwable);
        }
    }
}