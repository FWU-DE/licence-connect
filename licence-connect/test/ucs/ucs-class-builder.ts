import { UcsClass, UcsLicence, UcsStudentContext } from '@ucs/domain/ucs-types';
import { UcsStudentContextId } from '@ucs/infrastructure/dto/ucs-type.dto';
import { randomUUID } from 'crypto';

export class UcsClassBuilder {
  private class: UcsClass;

  private constructor(className: string, id = randomUUID().toString()) {
    this.class = UcsClassBuilder.createClass(className, id);
  }

  public static createClassWithName(className: string): UcsClassBuilder {
    const builder = new UcsClassBuilder(className);
    return builder;
  }

  public static createWithDefaultclass(): UcsClassBuilder {
    return UcsClassBuilder.createClassWithName('defaultClass');
  }

  public withId(id: string): UcsClassBuilder {
    this.class.id = id;
    return this;
  }

  public withLicence(licence: UcsLicence): UcsClassBuilder {
    this.class.licenses = this.class.licenses
      ? [...this.class.licenses, licence]
      : [licence];
    return this;
  }

  public getClass(): UcsClass {
    return this.class;
  }

  private static createClass(className: string, id = randomUUID().toString()) {
    return { name: className, id: id };
  }
}
