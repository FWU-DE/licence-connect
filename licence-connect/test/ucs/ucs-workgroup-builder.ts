import { UcsWorkgroup, UcsLicence } from '@ucs/domain/ucs-types';
import { randomUUID } from 'crypto';

export class UcsWorkgroupBuilder {
  private workgroup: UcsWorkgroup;

  private constructor(workgroupName: string, id = randomUUID().toString()) {
    this.workgroup = UcsWorkgroupBuilder.createWorkgroup(workgroupName, id);
  }

  public static createWorkgroupWithName(
    workgroupName: string,
  ): UcsWorkgroupBuilder {
    const builder = new UcsWorkgroupBuilder(workgroupName);
    return builder;
  }

  public static createWithDefaultWorkgroup(): UcsWorkgroupBuilder {
    return UcsWorkgroupBuilder.createWorkgroupWithName('defaultWorkgroup');
  }

  public withLicence(licence: UcsLicence): UcsWorkgroupBuilder {
    this.workgroup.licenses = this.workgroup.licenses
      ? [...this.workgroup.licenses, licence]
      : [licence];
    return this;
  }

  public withId(id: string): UcsWorkgroupBuilder {
    this.workgroup.id = id;
    return this;
  }

  public getWorkgroup(): UcsWorkgroup {
    return this.workgroup;
  }

  private static createWorkgroup(
    workgroupName: string,
    id = randomUUID().toString(),
  ) {
    return { name: workgroupName, id: id };
  }
}
