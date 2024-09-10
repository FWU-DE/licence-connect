import { UcsStudentContextId } from '@ucs/infrastructure/dto/ucs-type.dto';
import { UcsStudentRepository } from '../domain/ucs-student-repository';
import {
  UcsStudentId,
  UcsStudent,
  UcsStudentContext,
} from '../domain/ucs-types';

export class UCSStudentFromUCSStudentId {
  constructor() {}

  public async execute(
    ucsLicencesSource: UcsStudentRepository,
    ucsStudentId: UcsStudentId,
    ucsSchoolContext?: UcsStudentContextId,
  ): Promise<UcsStudent> {
    let ucsStudent = await ucsLicencesSource.getUCSStudentFromId(ucsStudentId);

    ucsStudent = this.filterSchoolContextsInStudent(
      ucsStudent,
      ucsSchoolContext,
    );

    return ucsStudent;
  }

  private filterSchoolContextsInStudent(
    ucsStudent: UcsStudent,
    relevantUcsSchoolContext?: UcsStudentContextId,
  ): UcsStudent {
    const selectAllSchoolContexts = relevantUcsSchoolContext === undefined;
    if (selectAllSchoolContexts) {
      return ucsStudent;
    }

    const relevantContext = Object.entries(ucsStudent.context).filter(
      ([contextId, schoolContext]) =>
        this.isContextRelevant(
          relevantUcsSchoolContext as UcsStudentContextId,
          {
            contextId,
            schoolContext,
          },
        ),
    );

    ucsStudent.context = {};

    relevantContext.forEach(
      ([contextId, studentContext]) =>
        (ucsStudent.context[contextId] = studentContext),
    );

    return ucsStudent;
  }

  private isContextRelevant(
    relevantSchoolContextIdentifier: UcsStudentContextId,
    studentContext: {
      contextId: UcsStudentContextId;
      schoolContext: UcsStudentContext;
    },
  ): boolean {
    return studentContext.contextId === relevantSchoolContextIdentifier;
  }
}
