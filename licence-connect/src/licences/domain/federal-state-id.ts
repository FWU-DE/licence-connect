export const simpleFederalStateIdentificationStrings = [
  'MV',
  'RP',
  'BW',
  'BY',
  'BE',
  'BB',
  'HB',
  'HH',
  'HE',
  'NI',
  'NW',
  'SL',
  'SN',
  'ST',
  'SH',
  'TH',
];
export type SimpleFederalStateAbbreviationString =
  (typeof simpleFederalStateIdentificationStrings)[number];

export const prefixedFederalStateAbbreviationStrings =
  simpleFederalStateIdentificationStrings.map((id: string) => 'DE-' + id);
export type PrefixedFederalStateAbbreviationString =
  (typeof prefixedFederalStateAbbreviationStrings)[number];

export const allFederalStateAbbreviations = [
  ...simpleFederalStateIdentificationStrings,
  ...prefixedFederalStateAbbreviationStrings,
];

export type FederalStateAbbreviation =
  (typeof allFederalStateAbbreviations)[number];
