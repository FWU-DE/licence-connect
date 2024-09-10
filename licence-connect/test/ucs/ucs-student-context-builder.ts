import { UcsLicence, UcsStudentContext } from '@ucs/domain/ucs-types';
import { UcsStudentContextId } from '@ucs/infrastructure/dto/ucs-type.dto';
import { UcsClassBuilder } from './ucs-class-builder';
import { UcsWorkgroupBuilder } from './ucs-workgroup-builder';

export class UcsStudentContextBuilder {
  private contextId: UcsStudentContextId;
  private context: UcsStudentContext;

  private constructor(contextId: UcsStudentContextId) {
    this.contextId = contextId;
    this.context = { roles: [] };
  }

  public static createContextWithId(
    contextId: UcsStudentContextId,
  ): UcsStudentContextBuilder {
    const builder = new UcsStudentContextBuilder(contextId);
    builder.context = UcsStudentContextBuilder.createDefaultContext();
    return builder;
  }

  public static createWithDefaultContext(): UcsStudentContextBuilder {
    return UcsStudentContextBuilder.createContextWithId(
      'oaijfds32f09sjef32rfasd',
    );
  }

  public getContext(): [UcsStudentContextId, UcsStudentContext] {
    return [this.contextId, this.context];
  }

  public withLicence(licence: UcsLicence): UcsStudentContextBuilder {
    this.context.licenses = this.context.licenses
      ? [...this.context.licenses, licence]
      : [licence];
    return this;
  }

  public withClassBuilderFunction(
    classBuilderFactory: () => UcsClassBuilder,
  ): UcsStudentContextBuilder {
    const classBuilder = classBuilderFactory();
    return this.withClassBuilder(classBuilder);
  }

  public withClassBuilder(
    classBuilder: UcsClassBuilder,
  ): UcsStudentContextBuilder {
    const ucsClass = classBuilder.getClass();
    this.context.classes = this.context.classes
      ? [...this.context.classes, ucsClass]
      : [ucsClass];
    return this;
  }

  public withWorkgroupBuilderFunction(
    classBuilderFactory: () => UcsWorkgroupBuilder,
  ): UcsStudentContextBuilder {
    const classBuilder = classBuilderFactory();
    return this.withWorkgroupBuilder(classBuilder);
  }

  public withWorkgroupBuilder(
    classBuilder: UcsWorkgroupBuilder,
  ): UcsStudentContextBuilder {
    const ucsWorkgroup = classBuilder.getWorkgroup();
    this.context.classes = this.context.classes
      ? [...this.context.classes, ucsWorkgroup]
      : [ucsWorkgroup];
    return this;
  }

  private static createDefaultContext() {
    return {
      school_authority: 'BBK_IN',
      school_identifier: 'HE-135109231418124',
      school_name: 'Schule A',
      roles: ['student'],
    };
  }
}
