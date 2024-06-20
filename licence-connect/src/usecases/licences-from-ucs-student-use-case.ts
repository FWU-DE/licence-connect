import { AvailableLicenses, License } from 'domain/licence';
import { UCSStudent } from 'domain/ucs/ucs-types';

export class LicencesFromUcsStudendUseCase {
  constructor() {}

  public execute(ucsStudent: UCSStudent): AvailableLicenses {
    return this.extractLicenceData(ucsStudent);
  }

  private extractLicenceData(student: UCSStudent): AvailableLicenses {
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
