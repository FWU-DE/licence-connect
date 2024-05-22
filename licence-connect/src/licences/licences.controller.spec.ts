import { Test, TestingModule } from '@nestjs/testing';
import { LicencesController } from './licences.controller';

describe('LicencesController', () => {
  let licencesController: LicencesController;

  beforeEach(async () => {
    const app: TestingModule = await Test.createTestingModule({
      controllers: [LicencesController],
      providers: [],
    }).compile();

    licencesController = app.get<LicencesController>(LicencesController);
  });

  it('should be created"', () => {
    expect(licencesController).toBeTruthy;
  });
});
