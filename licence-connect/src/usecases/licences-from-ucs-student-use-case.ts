import { AvailableLicences, Licence } from '@domain/Licence';
import { UCSStudent } from '@domain/ucs/ucs-types';

// NOTE: in UCS the Spelling is "license" while LicencesConnect uses the "licence" spelling
/**
 * Use Case for fetching LicenceConnect Licences for a specific User in an UCS system.
 * First the User is fetched, then the UCS licenses are collected and the LC licences are created from them
 */
export class LicencesFromUcsStudentUseCase {
  constructor() {}

  public execute(ucsStudent: UCSStudent): AvailableLicences {
    return this.extractLicenceData(ucsStudent);
  }

  private extractLicenceData(student: UCSStudent): AvailableLicences {
    const licencesAssignedToStudent = this.extractLicencesFromStudent(student);
    const licencesAssignedToContext =
      this.extractLicencesFromStudentsContext(student);
    const licencesAssignedToClasses =
      this.extractLicencesFromStudentsClasses(student);
    const licencesAssignedToWorkgroups =
      this.extractLicencesFromStudentsWorkgroups(student);

    const accumulatedLicences = [
      ...licencesAssignedToStudent,
      ...licencesAssignedToContext,
      ...licencesAssignedToClasses,
      ...licencesAssignedToWorkgroups,
    ];

    return accumulatedLicences;
  }

  private extractLicencesFromStudent(student: UCSStudent): AvailableLicences {
    return (
      student.licenses?.map((licence) => this.createLicenceObject(licence)) ??
      []
    );
  }

  private extractLicencesFromStudentsContext(
    student: UCSStudent,
  ): AvailableLicences {
    const studentContexts = this.extractStudentContexts(student);
    return studentContexts
      .filter((context) => !!context.licenses)
      .flatMap((context) =>
        context.licenses.map((licence) => this.createLicenceObject(licence)),
      );
  }

  private extractLicencesFromStudentsClasses(
    student: UCSStudent,
  ): AvailableLicences {
    const studentContexts = this.extractStudentContexts(student);
    return studentContexts.flatMap((context) =>
      context.classes
        .filter((ucsClass) => !!ucsClass.licenses)
        .flatMap((ucsClass) =>
          ucsClass.licenses.map((licence) => this.createLicenceObject(licence)),
        ),
    );
  }

  private extractLicencesFromStudentsWorkgroups(
    student: UCSStudent,
  ): AvailableLicences {
    const studentContexts = this.extractStudentContexts(student);
    return studentContexts.flatMap((context) =>
      context.workgroups
        .filter((ucsWorkgroup) => !!ucsWorkgroup.licenses)
        .flatMap((ucsWorkgroup) =>
          ucsWorkgroup.licenses.map((licence) =>
            this.createLicenceObject(licence),
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

  private createLicenceObject(licenceId: string): Licence {
    return {
      license_code: licenceId,
    };
  }
}
