import { UcsLicence, UcsStudent } from '@ucs/domain/ucs-types';
import { StudentId } from '@vidis/domain/student';
import { UcsStudentContextBuilder } from './ucs-student-context-builder';
import { UcsClassBuilder } from './ucs-class-builder';
import { UcsWorkgroupBuilder } from './ucs-workgroup-builder';

export const DEFAULT_STUDENT_NAME = 'defaultStudent';

export class UcsStudentBuilder {
  private student: UcsStudent;

  private constructor(studentId: StudentId) {
    this.student = this.createStudent(studentId);
  }

  public static forStudentWithId(studentId: StudentId): UcsStudentBuilder {
    const builder = new UcsStudentBuilder(studentId);
    return builder;
  }

  public static forDefaultStudent(): UcsStudentBuilder {
    const studentBuilder =
      UcsStudentBuilder.forStudentWithId(DEFAULT_STUDENT_NAME);
    studentBuilder
      .withContextBuilderFunction(() =>
        UcsStudentContextBuilder.createContextWithId('oaijfds32f09sjef32rfasd')
          .withClassBuilder(
            UcsClassBuilder.createClassWithName('Class1')
              .withId('239we423-wetiwrg23-sd0gfsd34')
              .withLicence('CCB-7bd46a45-345c-4237-a451-4444736eb011')
              .withLicence('CCB-2f57ko96-d32r-53wrw-3eqg-999f2242jgr45'),
          )
          .withClassBuilder(
            UcsClassBuilder.createClassWithName('Class2').withId(
              'f2309jfde13jfad0fj13rjal97',
            ),
          )
          .withWorkgroupBuilder(
            UcsWorkgroupBuilder.createWorkgroupWithName('mathegruppe1')
              .withId('f2309jfde13jfad0fj13rjal97')
              .withLicence('COR-3rw46a45-345c-4237-a451-4333736ex015')
              .withLicence('EKV-g54sn7bs-d32r-5s3643eqg-999a2dok3rts'),
          )
          .withWorkgroupBuilder(
            UcsWorkgroupBuilder.createWorkgroupWithName('mathegruppe2').withId(
              'f2309jfde13jfad0fj13rjal97',
            ),
          )
          .withLicence('HEL-72rh4f6c-f3gs-a4thg-f393tjse1kjso34w45fq'),
      )
      .withLicence('VHT-9234814-fk68-acbj6-3o9jyfilkq2pqdmxy0j')
      .withLicence('WES-234efsy-qer3-sdf35-ysdfpcjo3ijda09s2ds');
    return studentBuilder;
  }

  public withContextBuilderFunction(
    contextBuilderFactory: () => UcsStudentContextBuilder,
  ): UcsStudentBuilder {
    const contextBuilder = contextBuilderFactory();
    return this.withContextBuilder(contextBuilder);
  }

  public withContextBuilder(
    contextBuilder: UcsStudentContextBuilder,
  ): UcsStudentBuilder {
    const [contextId, context] = contextBuilder.getContext();
    this.student.context[contextId] = context;
    return this;
  }

  public withLicence(licence: UcsLicence): UcsStudentBuilder {
    this.student.licenses = this.student.licenses
      ? [...this.student.licenses, licence]
      : [licence];
    return this;
  }

  public get(): UcsStudent {
    return this.student;
  }

  private createStudent(studentId: StudentId) {
    return {
      id: studentId,
      first_name: 'Sample',
      last_name: 'User',
      context: {},
    };
  }
}
