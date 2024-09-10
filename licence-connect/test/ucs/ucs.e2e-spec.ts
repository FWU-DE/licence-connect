import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { INestApplication } from '@nestjs/common';
import { AppModule } from '../../src/app.module';
import { TEST_ENV_VARIABLES } from '../test-env';
import { UcsLicenseFetcherService } from '@ucs/infrastructure/ucs-license-fetcher-service/ucs-license-fetcher-service.service';
import { UcsStudentBuilder } from './ucs-student-builder';
import { UcsStudentContextBuilder } from './ucs-student-context-builder';
import { UcsClassBuilder } from './ucs-class-builder';
import { UcsWorkgroupBuilder } from './ucs-workgroup-builder';

const ucsLicenceKey = TEST_ENV_VARIABLES.VIDIS_API_KEY;

const contextIdentifier = 'oaijfds32f09sjef32rfasd';

const createStudentWithId = (studentId: string) =>
  UcsStudentBuilder.forStudentWithId(studentId)
    .withContextBuilderFunction(() =>
      UcsStudentContextBuilder.createContextWithId(contextIdentifier)
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
    .get();

describe('UCS (e2e)', () => {
  let app: INestApplication;

  process.env = { ...process.env, ...TEST_ENV_VARIABLES };

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    })
      .overrideProvider(UcsLicenseFetcherService)
      .useValue({
        fetchUcsStudentFromId: jest.fn((studentId: string) =>
          Promise.resolve(createStudentWithId(studentId)),
        ),
      })
      .compile();

    app = moduleFixture.createNestApplication();
    await app.init();
  });

  describe('API KEY v1/ucs/request', () => {
    describe('Without Context Identifier', () => {
      it('should return all licences', () => {
        return request(app.getHttpServer())
          .post(`/ucs/request`)
          .set({ 'X-API-KEY': ucsLicenceKey })
          .send({
            userId: 'student.42',
            clientId: 'sodix-editor-o',
            federalState: 'DE-MV',
          })
          .expect(200)
          .expect(createStudentWithId('student.42'));
      });
    });

    describe('With Context Identifier', () => {
      it('should return all licences (identifier do matches)', () => {
        return request(app.getHttpServer())
          .post(`/ucs/request`)
          .set({ 'X-API-KEY': ucsLicenceKey })
          .send({
            userId: 'student.42',
            clientId: 'sodix-editor-o',
            ucsContextIdentifier: contextIdentifier,
            federalState: 'DE-MV',
          })
          .expect(200)
          .expect(createStudentWithId('student.42'));
      });

      it('should return no licences (identifier do not matches)', () => {
        return request(app.getHttpServer())
          .post(`/ucs/request`)
          .set({ 'X-API-KEY': ucsLicenceKey })
          .send({
            userId: 'student.42',
            clientId: 'sodix-editor-o',
            ucsContextIdentifier: contextIdentifier + 'noMatch',
            federalState: 'DE-MV',
          })
          .expect(200)
          .expect(
            UcsStudentBuilder.forStudentWithId('student.42')
              .withLicence('VHT-9234814-fk68-acbj6-3o9jyfilkq2pqdmxy0j')
              .get(),
          );
      });
    });
  });
});

describe('UCS (e2e)', () => {
  let app: INestApplication;

  process.env = { ...process.env, ...TEST_ENV_VARIABLES };

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    })
      .overrideProvider(UcsLicenseFetcherService)
      .useValue({
        fetchUcsStudentFromId: jest.fn((studentId: string) =>
          Promise.resolve(
            UcsStudentBuilder.forStudentWithId(studentId)
              .withContextBuilderFunction(() =>
                UcsStudentContextBuilder.createContextWithId(contextIdentifier)
                  .withClassBuilder(
                    UcsClassBuilder.createClassWithName('Class1')
                      .withId('239we423-wetiwrg23-sd0gfsd34')
                      .withLicence('CCB-7bd46a45-345c-4237-a451-4444736eb011')
                      .withLicence(
                        'CCB-2f57ko96-d32r-53wrw-3eqg-999f2242jgr45',
                      ),
                  )
                  .withLicence('HEL-72rh4f6c-f3gs-a4thg-f393tjse1kjso34w45fq'),
              )
              .withContextBuilderFunction(() =>
                UcsStudentContextBuilder.createContextWithId('shouldNotShowUp')
                  .withClassBuilder(
                    UcsClassBuilder.createClassWithName('Class2')
                      .withId('111we423-wetiwrg23-sd0gfsd34')
                      .withLicence('CCB-7bd46a45-345c-4237-a451-4444736eb011')
                      .withLicence(
                        'CCB-2f57ko96-d56r-53wrw-3eqg-999f2242jgr45',
                      ),
                  )
                  .withLicence('HEL-72rh4f6c-f3gs-a4thg-f393tjse1kjso34w45fq'),
              )
              .withLicence('VHT-9234814-fk68-acbj6-3o9jyfilkq2pqdmxy0j')
              .get(),
          ),
        ),
      })
      .compile();

    app = moduleFixture.createNestApplication();
    await app.init();
  });

  describe('API KEY v1/ucs/request', () => {
    describe('With Context Identifier', () => {
      it('should return dedicated licences (not all identifier matches)', () => {
        return request(app.getHttpServer())
          .post(`/ucs/request`)
          .set({ 'X-API-KEY': ucsLicenceKey })
          .send({
            userId: 'student.42',
            clientId: 'sodix-editor-o',
            ucsContextIdentifier: contextIdentifier,
            federalState: 'DE-MV',
          })
          .expect(200)
          .expect(
            UcsStudentBuilder.forStudentWithId('student.42')
              .withContextBuilderFunction(() =>
                UcsStudentContextBuilder.createContextWithId(contextIdentifier)
                  .withClassBuilder(
                    UcsClassBuilder.createClassWithName('Class1')
                      .withId('239we423-wetiwrg23-sd0gfsd34')
                      .withLicence('CCB-7bd46a45-345c-4237-a451-4444736eb011')
                      .withLicence(
                        'CCB-2f57ko96-d32r-53wrw-3eqg-999f2242jgr45',
                      ),
                  )
                  .withLicence('HEL-72rh4f6c-f3gs-a4thg-f393tjse1kjso34w45fq'),
              )
              .withLicence('VHT-9234814-fk68-acbj6-3o9jyfilkq2pqdmxy0j')
              .get(),
          );
      });
    });
  });
});
