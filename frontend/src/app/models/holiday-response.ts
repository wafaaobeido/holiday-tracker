import { Holiday } from "./holiday";

export interface HolidayResponse {
    data: { [countryCode: string]: Holiday[] };
    errors: { [countryCode: string]: string };
  }