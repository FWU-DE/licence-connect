import { Injectable } from '@nestjs/common';
import { ResponseFromUCS, UCSStudent } from '../UCSTypes';
import { AvailableLicenses, License } from '../../../domain/Licence';

/**
 * Licence Service providing access to the UCS system of Mecklenburg-Vorpommern
 */
@Injectable()
export class MVLicenceService {
  constructor() {}

  public extractLicenceData(ucsStudent: ResponseFromUCS): AvailableLicenses {
    const student = ucsStudent['http://www.bildungslogin.de/licenses'];

    const licensesAssignedToStudent = this.extractLicensesFromStudent(student);
    const licensesAssignedToContext =
      this.extractLicensesFromStudentsContext(student);
    const licensesAssignedToClasses =
      this.extractLicensesFromStudentsClasses(student);
    const licensesAssignedToWorkgroups =
      this.extractLicensesFromStudentsWorkgroups(student);

    const accumulatedLicences = [
      ...licensesAssignedToStudent,
      ...licensesAssignedToContext,
      ...licensesAssignedToClasses,
      ...licensesAssignedToWorkgroups,
    ];

    return accumulatedLicences;
  }

  private extractLicensesFromStudent(student: UCSStudent): AvailableLicenses {
    return (
      student.licenses?.map((license) => this.createLicenseObject(license)) ??
      []
    );
  }

  private extractLicensesFromStudentsContext(
    student: UCSStudent,
  ): AvailableLicenses {
    const studentContexts = this.extractStudentContexts(student);
    return studentContexts
      .filter((context) => !!context.licenses)
      .flatMap((context) =>
        context.licenses.map((license) => this.createLicenseObject(license)),
      );
  }

  private extractLicensesFromStudentsClasses(
    student: UCSStudent,
  ): AvailableLicenses {
    const studentContexts = this.extractStudentContexts(student);
    return studentContexts.flatMap((context) =>
      context.classes
        .filter((ucsClass) => !!ucsClass.licenses)
        .flatMap((ucsClass) =>
          ucsClass.licenses.map((license) => this.createLicenseObject(license)),
        ),
    );
  }

  private extractLicensesFromStudentsWorkgroups(
    student: UCSStudent,
  ): AvailableLicenses {
    const studentContexts = this.extractStudentContexts(student);
    return studentContexts.flatMap((context) =>
      context.workgroups
        .filter((ucsWorkgroup) => !!ucsWorkgroup.licenses)
        .flatMap((ucsWorkgroup) =>
          ucsWorkgroup.licenses.map((license) =>
            this.createLicenseObject(license),
          ),
        ),
    );
  }

  private extractStudentContexts(student: UCSStudent) {
    const studentContexts = Object.keys(student.context).map(
      (key) => student.context[key],
    );
    return studentContexts;
  }

  private createLicenseObject(licenseId: string): License {
    return {
      licenseId: licenseId,
    };
  }
}
