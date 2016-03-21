extern "C"
__global__ void ipc(double *CMass, double *SMass, double *HMass, double *NMass, double *OMass, double *CP, double *SP, double *HP, double *NP, double *OP, int *arrayCombination, int *indexCombination, double *MassOutput, double *POutput)
{

	int cMax = arrayCombination[0];
	int sMax = arrayCombination[1];
	int hMax = arrayCombination[2];
	int nMax = arrayCombination[3];
	int oMax = arrayCombination[4];
	
	double *LocalCMass = new double[cMax];
	double *LocalSMass = new double[sMax];
	double *LocalHMass = new double[hMax];
	double *LocalNMass = new double[nMax];
	double *LocalOMass = new double[oMax];
	
	double *LocalCP = new double[cMax];
	double *LocalSP = new double[sMax];
	double *LocalHP = new double[hMax];
	double *LocalNP = new double[nMax];
	double *LocalOP = new double[oMax];
	
	int index = blockIdx.x;
	int indexC = index * 5;
	int indexS = index * 5 + 1;
	int indexH = index * 5 + 2;
	int indexN = index * 5 + 3;
	int indexO = index * 5 + 4;
	
	int CNumber = indexCombination[indexC];
	int SNumber = indexCombination[indexS];
	int HNumber = indexCombination[indexH];
	int NNumber = indexCombination[indexN];
	int ONumber = indexCombination[indexO];
	
	for(int i = 0; i != cMax; i++){
		LocalCMass[i] = CMass[cMax * (CNumber - 1) + i];
		LocalCP[i] = CP[cMax * (CNumber - 1) + i];
	}
	for(int i = 0; i != sMax; i++){
		if(SNumber > 0){
			LocalSMass[i] = SMass[sMax * (SNumber - 1) + i];
			LocalSP[i] = SP[sMax * (SNumber - 1) + i];
		}else{
			LocalSMass[i] = 0;
			LocalSP[i] = 1;
		}
	}
	for(int i = 0; i != hMax; i++){
		LocalHMass[i] = HMass[hMax * (HNumber - 1) + i];
		LocalHP[i] = HP[hMax * (HNumber - 1) + i];
	}
	for(int i = 0; i != nMax; i++){
		LocalNMass[i] = NMass[nMax * (NNumber - 1) + i];
		LocalNP[i] = NP[nMax * (NNumber - 1) + i];
	}
	for(int i = 0; i != oMax; i++){
		LocalOMass[i] = OMass[oMax * (ONumber - 1) + i];
		LocalOP[i] = OP[oMax * (ONumber - 1) + i];
	}
	
	int count = 0;
	int total = cMax * sMax * hMax * nMax * oMax;
	for(int i = 0; i != cMax; i++){
		for(int j = 0; j != sMax; j++){
			for(int k = 0; k != hMax; k++){
				for(int l = 0; l != nMax; l++){
					for(int m = 0; m != oMax; m++){
						if(SNumber > 0){
							MassOutput[total * index + count] = LocalCMass[i] + LocalSMass[j] + LocalHMass[k] + LocalNMass[l] + LocalOMass[m] + 5 * 0.00054858;
						}else{
							MassOutput[total * index + count] = LocalCMass[i] + LocalSMass[j] + LocalHMass[k] + LocalNMass[l] + LocalOMass[m] + 4 * 0.00054858;
						}
						
						POutput[total * index + count] = LocalCP[i] * LocalSP[j] * LocalHP[k] * LocalNP[l] * LocalOP[m];
						count++;
					}
				}
			}
		}
	}
}
